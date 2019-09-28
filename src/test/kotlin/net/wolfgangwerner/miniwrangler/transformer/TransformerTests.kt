package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.*
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.text.ParseException
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.concurrent.atomic.AtomicInteger

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformerTests {
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
    private val testFile = File("src/test/resources/orders.csv")

    private fun exampleConfig() = TransformationConfig().apply { columns.addAll(exampleHeaders) }

    @Test
    fun `Transformer transforms complete file`() {
        val resultCollection = mutableListOf<Map<String, Any>>()
        val collectingHandler: (Map<String, Any>) -> Unit = { r: Map<String, Any> -> resultCollection.add(r) }

        val config = exampleConfig()
        val field = StringField("foo")
        field.columnRefs.add("Order Number")
        config.add(field)
        val transformer = Transformer(config, collectingHandler)
        transformer.transform(testFile)

        assertThat(resultCollection.size).isEqualTo(2)
        assertThat(resultCollection).contains(mapOf("foo" to "1000"))
        assertThat(resultCollection).contains(mapOf("foo" to "1001"))
    }


    @Test
    fun `Transformer supports mixed-type records`() {
        val resultCollection = mutableListOf<Map<String, Any>>()
        val collectingHandler: (Map<String, Any>) -> Unit = { r: Map<String, Any> -> resultCollection.add(r) }

        val config = exampleConfig()
        val iField = IntegerField("i")
        iField.columnRefs.add("Order Number")

        val sField = StringField("s", "", "", ": ")
        sField.columnRefs.add("Product Number")
        sField.columnRefs.add("Product Name")

        val dField = DateField("d")
        dField.columnRefs.add("Year")
        dField.columnRefs.add("Month")
        dField.columnRefs.add("Day")

        val vField = StaticStringValueField("v", "X")

        config.add(iField)
        config.add(sField)
        config.add(dField)
        config.add(vField)

        val transformer = Transformer(config, collectingHandler)
        transformer.transform(testFile)

        assertThat(resultCollection.size).isEqualTo(2)
        assertThat(resultCollection).contains(
            mapOf(
                "i" to 1000,
                "s" to "P-10001: Arugola",
                "d" to LocalDate.of(2018, 1, 1),
                "v" to "X"
            )
        )
        assertThat(resultCollection).contains(
            mapOf(
                "i" to 1001,
                "s" to "P-10002: Iceberg lettuce",
                "d" to LocalDate.of(2017, 12, 12),
                "v" to "X"
            )
        )
    }

    @Test
    fun `Transformer supports mapping columns twice`() {
        val resultCollection = mutableListOf<Map<String, Any>>()
        val collectingHandler: (Map<String, Any>) -> Unit = { r: Map<String, Any> -> resultCollection.add(r) }

        val config = exampleConfig()
        val iField = IntegerField("i")
        iField.columnRefs.add("Order Number")

        val sField = StringField("s")
        sField.columnRefs.add("Order Number")

        config.add(iField)
        config.add(sField)


        val transformer = Transformer(config, collectingHandler)
        transformer.transform(testFile)

        assertThat(resultCollection.size).isEqualTo(2)
        assertThat(resultCollection).contains(mapOf("i" to 1000, "s" to "1000"))
        assertThat(resultCollection).contains(mapOf("i" to 1001, "s" to "1001"))
    }

    @Test
    fun `Transformer reports invalid formatted date field`() {
        val invalidValue = "this is not a date"
        val result = testFieldValidation(
            FormattedDateField("FD", "dd.MM.yyyy"),
            "01.01.2001",
            invalidValue
        )

        assertThat(result.first).isEqualTo(1)
        assertThat(result.second).isInstanceOf(DateTimeParseException::class.java)
        assertThat(result.third).isEqualTo(arrayOf(invalidValue))
    }


    @Test
    fun `Transformer reports invalid integer field`() {
        val invalidValue = "fortytwo"
        val result = testFieldValidation(
            IntegerField("I"),
            "42",
            invalidValue
        )

        assertThat(result.first).isEqualTo(1)
        assertThat(result.second).isInstanceOf(NumberFormatException::class.java)
        assertThat(result.third).isEqualTo(arrayOf(invalidValue))
    }

    @Test
    fun `Transformer reports invalid formatted decimal field`() {
        val invalidValue = "not a decimal"
        val result = testFieldValidation(
            DecimalField("d", "###.##"),
            "12.34",
            invalidValue
        )

        assertThat(result.first).isEqualTo(1)
        assertThat(result.second).isInstanceOf(ParseException::class.java)
        assertThat(result.third).isEqualTo(arrayOf(invalidValue))
    }

    private fun testFieldValidation(
        field: RecordField<Any>, validValue: String, invalidValue: String
    ): Triple<Int, Exception?, Array<String>> {
        var errorException: Exception? = null
        var errorRow: Array<String> = arrayOf()
        val errorCount: AtomicInteger = AtomicInteger(0)

        val errorHandler: (Array<String>, Exception) -> Unit = { row, e ->
            errorCount.getAndIncrement()
            errorException = e
            errorRow = row
        }

        val config = TransformationConfig().apply {
            columns.add("D")
            recordFields.add(field.apply {
                columnRefs.add("D")
            })
        }

        val tmpFile = File.createTempFile("test", ".csv")
        tmpFile.writeText("D\n$validValue\n$invalidValue\n$validValue")
        val transformer = Transformer(config, {}, errorHandler)
        transformer.transform(tmpFile)
        return Triple(errorCount.get(), errorException, errorRow)
    }

    @Test
    fun `Transformer reports invalid date field`() {
        var errorException: Exception? = null
        var errorRow: Array<String> = arrayOf()
        val errorCount: AtomicInteger = AtomicInteger(0)

        val errorHandler: (Array<String>, Exception) -> Unit = { row, e ->
            errorCount.getAndIncrement()
            errorException = e
            errorRow = row
        }

        val config = TransformationConfig().apply {
            columns.addAll(arrayOf("D", "M", "Y"))
            recordFields.add(DateField("D").apply {
                columnRefs.add("Y")
                columnRefs.add("M")
                columnRefs.add("D")
            })
        }

        val tmpFile = File.createTempFile("test", ".csv")
        tmpFile.writeText("D,M,Y\n1,1,2001\n06,5,1979\nthis is,not,a date\n28,11,2009")
        val transformer = Transformer(config, {}, errorHandler)
        transformer.transform(tmpFile)

        assertThat(errorCount.get()).isEqualTo(1)
        assertThat(errorException).isInstanceOf(DateTimeParseException::class.java)
        assertThat(errorRow).isEqualTo(arrayOf("this is", "not", "a date"))
    }

    @Test
    fun `Transformer reports malformed rows`() {
        var errorException: Exception? = null
        var errorRow: Array<String> = arrayOf()
        val errorCount: AtomicInteger = AtomicInteger(0)

        val errorHandler: (Array<String>, Exception) -> Unit = { row, e ->
            errorCount.getAndIncrement()
            errorException = e
            errorRow = row
        }

        val config = TransformationConfig().apply {
            columns.addAll(arrayOf("D", "M", "Y"))
            recordFields.add(DateField("D").apply {
                columnRefs.add("Y")
                columnRefs.add("M")
                columnRefs.add("D")
            })
        }

        val tmpFile = File.createTempFile("test", ".csv")
        tmpFile.writeText("D,M,Y\n1,1,2001\n06,5,1979\nthis is not a valid row\n28,11,2009")
        val transformer = Transformer(config, {}, errorHandler)
        transformer.transform(tmpFile)

        assertThat(errorCount.get()).isEqualTo(1)
        assertThat(errorException).isInstanceOf(IllegalStateException::class.java)
        assertThat(errorRow).isEqualTo(arrayOf("this is not a valid row"))
    }

}
