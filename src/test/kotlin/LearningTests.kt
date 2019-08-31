package test.kotlin

import com.github.javafaker.Faker
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.simpleflatmapper.csv.CsvParser
import java.io.File
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LearningTests {
    @Test
    @Disabled
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
    fun `can parse CSV as stream`() = runBlocking {
        val start = System.currentTimeMillis()

        File("src/test/resources/orders-10000.csv").bufferedReader().use {
            CsvParser
                .skip(1)
                .stream(it)
                .forEach { row ->
                    launch(Dispatchers.Default) {
                        log(asyncTransform(row).joinToString(","))
                    }
                }
        }
        log("Parsed input in ${System.currentTimeMillis() - start} ms")
    }


    @Test
    fun createTestFile() = runBlocking {
        val recordCount = 10_000
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

    private suspend fun asyncTransform(row: Array<String>): Array<String> = withContext(Dispatchers.Default) {
        transform(row)
    }

    private fun transform(row: String): String {
        Thread.sleep(1000)
        return "[transformed] $row"
    }

    private fun transform(row: Array<String>): Array<String> {
        //Thread.sleep(1000)
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
        channel.close()
    }

    fun CoroutineScope.transform(rows: ReceiveChannel<String>): ReceiveChannel<Deferred<String>> = produce {
        for (row in rows) {
            val transformedRow = async { asyncTransform(row) }
            send(transformedRow)
        }
    }

    fun log(v: Any) = println("[${Thread.currentThread().name}] $v")

}