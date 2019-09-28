package net.wolfgangwerner.miniwrangler.transformer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfigDsl
import net.wolfgangwerner.miniwrangler.model.record.Record
import org.simpleflatmapper.csv.CsvParser
import java.io.File
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@ExperimentalCoroutinesApi
class Transformer(
    private val config: TransformationConfig,
    val outputListener: (record: Map<String, Any>) -> Unit,
    val errorListener: (row: Array<String>, exception: Exception) -> Unit = { row, ex ->
        System.err.println(
            "ERROR: ${ex.message} in ${row.joinToString(", ", "[", "]")}"
        )
    }
) {
    init {
        config.ensureIsValid()
    }

    constructor(
        configFile: File,
        outputListener: (record: Map<String, Any>) -> Unit,
        errorListener: (row: Array<String>, exception: Exception) -> Unit = { row, ex ->
            System.err.println(
                "ERROR: ${ex.message} in ${row.joinToString(", ", "[", "]")}"
            )
        }
    ) : this(parseConfig(configFile), outputListener, errorListener)

    public fun transform(csvFile: File, sync: Boolean = false) {
        try {
            config.ensureIsValid()
            config.ensureCsvMatches(csvFile)
        } catch (e: Exception) {
            errorListener(arrayOf<String>(), e)
            throw e // rethrow as we can't start the transformation with invalid config/file
        }

        if (sync) syncTransform(csvFile)
        else asyncTransform(csvFile)
    }

    private fun asyncTransform(csvFile: File) {
        runBlocking {
            val rowInputChannel = produceCsvRowsFromFile(csvFile)
            val transformerChannel = transformerChannel(rowInputChannel, errorListener)
            transformerChannel.consumeEach { outputListener(it) }
        }
    }

    private fun syncTransform(csvFile: File) {
        csvFile.bufferedReader().use { rows ->
            CsvParser
                .skip(1)
                .stream(rows)
                .forEach { row ->
                    try {
                        outputListener(transform(row).value())
                    } catch (e: Exception) {
                        errorListener(row, e)
                    }
                }
        }
    }

    internal fun transform(row: Array<String>): Record {
        check(!(row.size !== config.columns.size)) {
            "Row columns do not match configuration: " + row.joinToString(",", "[", "]")
        }
        return Record(row, config)
    }


    private suspend fun asyncTransform(row: Array<String>) =
        withContext(Dispatchers.Default) {
            transform(row)
        }

    private fun CoroutineScope.produceCsvRowsFromFile(file: File) = produce<Array<String>> {
        file.bufferedReader().use {
            CsvParser
                .skip(1)
                .stream(it)
                .forEach { row -> launch(Dispatchers.Default) { send(row) } }
        }
    }

    private fun CoroutineScope.transformerChannel(
        inputChannel: ReceiveChannel<Array<String>>,
        errorListener: (row: Array<String>, exception: Exception) -> Unit
    ): ReceiveChannel<Map<String, Any>> = produce {
        for (row in inputChannel) {
            try {
                val transformed =
                    asyncTransform(row).value()
                send(transformed)
            } catch (e: Exception) {
                errorListener(row, e)
            }
        }
    }

    companion object {
        private val engine: ScriptEngine =
            ScriptEngineManager(Thread.currentThread().contextClassLoader).getEngineByExtension("kts")

        private fun parseConfig(configFile: File): TransformationConfig {
            try {
                val configDsl = engine.eval(configFile.readText()).takeIf { it is TransformationConfigDsl }
                    ?.let { it as TransformationConfigDsl }
                    ?: throw IllegalArgumentException("Cannot parse ${configFile.path} as TransformationConfiguration")

                return configDsl.config
            } catch (e: Exception) {
                throw IllegalStateException("Cannot load script", e)
            }
        }

    }

}

