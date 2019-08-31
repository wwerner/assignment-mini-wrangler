package test.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
    fun `can transform`() = runBlocking {
        val mockRecords = produceMockRecords()

        for (row in mockRecords) {
            log(transform(row))
        }
    }

    @Test
    fun `can transform async`() = runBlocking {
        val mockRecords = produceMockRecords()
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

    private fun transform(row: String): String {
        Thread.sleep(1000)
        return "[transformed] $row"
    }

    private fun CoroutineScope.produceMockRecords() = produce<String> {
        var cnt = 0
        while (cnt < 15) {
            send(
                "${1000 + cnt}," +
                        "${Random.nextInt(2016, 2020)}," +
                        "${Random.nextInt(1, 12)}," +
                        "${Random.nextInt(1, 31)}," +
                        "P-${Random.nextInt(10000, 15000)}," +
                        "Foo Bar Baz," +
                        "${Random.nextDouble(0.00, 1000.00)}," +
                        "Qux," +
                        "Quux"
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