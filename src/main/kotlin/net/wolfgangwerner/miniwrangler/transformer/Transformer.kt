package net.wolfgangwerner.miniwrangler.transformer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.Record
import org.simpleflatmapper.csv.CsvParser
import java.io.File

@ExperimentalCoroutinesApi
class Transformer(
        private val config: TransformationConfig,
        val outputListener: (record: Map<String, Any>) -> Unit,
        val errorListener: (row: Array<String>, exception: Exception) -> Unit = { row, ex -> println("ERROR: ${ex.message} in ${row.joinToString(", ")}") }) {

    public fun transform(csvFile: File) = runBlocking {
        val rowInputChannel = produceCsvRowsFromFile(csvFile)
        val transformerChannel = transformerChannel(rowInputChannel)
    }

    internal fun transform(row: Array<String>) {
        val result = Record(row, config)
        try {
            outputListener(result.value())
        } catch (e: Exception) {
            errorListener(row, e)
        }
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

    fun CoroutineScope.transformerChannel(inputChannel: ReceiveChannel<Array<String>>): ReceiveChannel<Deferred<Unit>> = produce {
        for (row in inputChannel) {
            async { asyncTransform(row) }
        }
    }

}
