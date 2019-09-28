package test.kotlin

import com.github.javafaker.Faker
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.simpleflatmapper.csv.CsvParser
import java.io.File
import kotlin.random.Random

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LearningTests {
    @Test
    fun `can read CSV into stream`() = runBlocking {
        File("src/test/resources/orders.csv").bufferedReader().useLines { rows ->
            rows.forEach {
                launch {
                    transform(it)
                }
            }
        }
    }

    @Test
    @Tag("slow")
    @Disabled("This test runs ~4h on an i7 8 core, only enable it if you really want to run the performance comparison")
    fun `compare sync and async processing`() = runBlocking {
        val recordCounts = arrayOf(100, 1000, 10000, 100000)
        val sleepTimes = arrayOf(0L, 10L, 100L)

        val results = StringBuilder("|===\n")
        results.append("|Rows|Transformation ms|Duration sync ms|Duration async m|async/syncs\n")


        for (recordCount in recordCounts) {
            createTestFile(recordCount)
            for (sleepTime in sleepTimes) {
                val testFile = "src/test/resources/orders-$recordCount.csv"
                val startAsync = System.currentTimeMillis()
                val mockRecords = produceCsvRecordsFromFile(testFile)
                val output = transformRecord(mockRecords, sleepTime)
                for (transformedRow in output) {
                    launch(Dispatchers.Default) {
                        log(transformedRow.await().joinToString(","))
                    }
                }
                val endAsync = System.currentTimeMillis()

                val startSync = System.currentTimeMillis()
                File(testFile).bufferedReader().use {
                    CsvParser
                            .skip(1)
                            .stream(it)
                            .forEach { row ->
                                log(transform(row, sleepTime).joinToString(","))
                            }
                }
                val endSync = System.currentTimeMillis()

                val syncDuration = endSync - startSync
                val asyncDuration = endAsync - startAsync
                val factor = asyncDuration.toFloat() / syncDuration.toFloat()
                results.append("|$recordCount|$sleepTime|$syncDuration|$asyncDuration|${factor}\n")

            }
        }
        results.append("|===")
        println(results)
    }


    private fun createTestFile(recordCount: Int) = runBlocking {
        val mockRecords = produceMockRecords(recordCount)

        File("src/test/resources/orders-$recordCount.csv").bufferedWriter().use { out ->
            out.write("Order Number,Year,Month,Day,Product Number,Product Name,Count,Extra Col1,Extra Col2,Empty Column\n")
            for (row in mockRecords) {
                out.write(row)
                out.write("\n")
            }
        }

    }


    @Test
    fun `can transform`() = runBlocking {
        val mockRecords = produceMockRecords(5)

        for (row in mockRecords) {
            log(transform(row))
        }
    }

    @Test
    fun `can transform async`() = runBlocking {
        val mockRecords = produceMockRecords(5)
        val output = transform(mockRecords)

        for (transformedRow in output) {
            launch(Dispatchers.Default) {
                log(transformedRow.await())
            }
        }
    }

    private suspend fun asyncTransform(row: String): String = withContext(Dispatchers.Default) {
        transform(row)
    }

    private suspend fun asyncTransform(row: Array<String>, duration: Long): Array<String> =
            withContext(Dispatchers.Default) {
                transform(row, duration)
            }

    private fun transform(row: String): String {
        return "[transformed] $row"
    }

    private fun transform(row: Array<String>, duration: Long?): Array<String> {
        if (duration !== null)
            Thread.sleep(duration)
        row[0] = "[transformed]" + row[0]
        return row
    }

    private fun CoroutineScope.produceMockRecords(count: Int) = produce<String> {
        val faker = Faker()

        var cnt = 0
        while (cnt < count) {
            send(
                    "${1000 + cnt}," +
                            "${Random.nextInt(2016, 2020)}," +
                            "${Random.nextInt(1, 12)}," +
                            "${Random.nextInt(1, 31)}," +
                            "P-${Random.nextInt(10000, 15000)}," +
                            "${faker.food().ingredient()}," +
                            "${faker.number().randomDouble(2, 0, 1000)}," +
                            "${faker.hitchhikersGuideToTheGalaxy().character()}," +
                            "${faker.hitchhikersGuideToTheGalaxy().planet()},"
            )
            cnt++;
        }
    }

    private fun CoroutineScope.produceCsvRecordsFromFile(file: String) = produce<Array<String>> {
        File(file).bufferedReader().use {
            CsvParser
                    .skip(1)
                    .stream(it)
                    .forEach { row -> launch(Dispatchers.Default) { send(row) } }
        }
    }

    fun CoroutineScope.transform(rows: ReceiveChannel<String>): ReceiveChannel<Deferred<String>> = produce {
        for (row in rows) {
            val transformedRow = async { asyncTransform(row) }
            send(transformedRow)
        }
    }

    fun CoroutineScope.transformRecord(
            rows: ReceiveChannel<Array<String>>,
            duration: Long
    ): ReceiveChannel<Deferred<Array<String>>> = produce {
        for (row in rows) {
            val transformedRow = async { asyncTransform(row, duration) }
            send(transformedRow)
        }
    }

    fun log(v: Any) = println("[${Thread.currentThread().name}] $v")

}
