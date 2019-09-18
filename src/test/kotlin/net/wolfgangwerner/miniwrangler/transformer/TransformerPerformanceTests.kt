package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import com.github.javafaker.Faker
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.*
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.simpleflatmapper.csv.CsvParser
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformerPerformanceTests {
    private val exampleHeaders = arrayOf(
        "Order Number",
        "Year",
        "Month",
        "Day",
        "Product Number",
        "Product Name",
        "Count",
        "Extra Col1",
        "Extra Col2",
        "Empty Column"
    )

    private fun exampleConfig() = TransformationConfig().apply {
        columns.addAll(exampleHeaders)
        recordFields.add(IntegerField("OrderID").apply { columns.add("Order Number") })
        recordFields.add(DateField("OrderDate").apply { columns.addAll(arrayOf("Year", "Month", "Day")) })
        recordFields.add(StringField("ProductId").apply { columns.addAll(arrayOf("Product Number")) })
        recordFields.add(StringField("ProductName").apply { columns.addAll(arrayOf("Product Name")) })
        recordFields.add(DecimalField("Quantity", "#,##0.0#").apply { columns.addAll(arrayOf("Count")) })
        recordFields.add(StaticStringValueField("Unit", "kg"))
    }

    @Test
    fun `compare sync and async processing`() = runBlocking {
        val recordCounts = arrayOf(100, 1_000, 10_000, 100_000, 1_000_000) // FIXME: split into one slow and one fast test
        val config = exampleConfig()

        val results = StringBuilder("|===\n")
        results.append("|Rows|Duration sync ms|Duration async ms|async/sync\n")

        for (recordCount in recordCounts) {
            val resultCount: AtomicInteger = AtomicInteger(0)
            val errorCount: AtomicInteger = AtomicInteger(0)

            val testFile = createTestFile(recordCount)
            val transformer = Transformer(
                config,
                { _: Map<String, Any> -> resultCount.getAndIncrement() },
                { _: Array<String>, _: Exception -> errorCount.getAndIncrement() }
            )

            val startSync = System.currentTimeMillis()
            transformer.transform(testFile, true)
            val endSync = System.currentTimeMillis()
            assertThat(resultCount.get() + errorCount.get()).isEqualTo(recordCount)


            resultCount.set(0)
            errorCount.set(0)
            val startAsync = System.currentTimeMillis()
            transformer.transform(testFile, false)
            val endAsync = System.currentTimeMillis()
            assertThat(resultCount.get() + errorCount.get()).isEqualTo(recordCount)


            val syncDuration = endSync - startSync
            val asyncDuration = endAsync - startAsync
            val factor = asyncDuration.toDouble() / syncDuration.toDouble()
            results.append("|$recordCount|$syncDuration|$asyncDuration|${factor}\n")

            //For quick (<10ms) row transformations, synchronous processing is faster. See README.
            //assertThat(factor).isLessThan(1.toDouble())
        }
        results.append("|===")
        println(results)
    }

    @Test
    fun `Transformer handles all rows`() {
        val inputRecordCount = 9999
        val resultCount: AtomicInteger = AtomicInteger(0)
        val errorCount: AtomicInteger = AtomicInteger(0)

        val testFile = createTestFile(inputRecordCount)

        val transformer = Transformer(
            exampleConfig(),
            { _: Map<String, Any> -> resultCount.getAndIncrement() },
            { _: Array<String>, _: Exception -> errorCount.getAndIncrement() }
        )
        transformer.transform(testFile)
        assertThat(resultCount.get() + errorCount.get()).isEqualTo(inputRecordCount)

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun createTestFile(recordCount: Int) = runBlocking {
        val mockRecords = produceMockRecords(recordCount)

        val tmpFile = File.createTempFile("test-orders", ".csv")

        tmpFile.bufferedWriter().use { out ->
            out.write("Order Number,Year,Month,Day,Product Number,Product Name,Count,Extra Col1,Extra Col2,Empty Column\n")
            for (row in mockRecords) {
                out.write(row)
                out.write("\n")
            }
        }
        return@runBlocking tmpFile
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

    fun log(v: Any) = println("[${Thread.currentThread().name}] $v")

}
