package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.*
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RowTransformationTests {
    val exampleHeaders = arrayOf("Order Number", "Year", "Month", "Day", "Product Number", "Product Name", "Count", "Extra Col1", "Extra Col2", "Empty Column")
    val exampleInput = arrayOf("1000", "2018", "1", "1", "P-10001", "Arugola", "5,250.50", "Lorem","Ipsum", "")
    private fun exampleConfig() = TransformationConfig().apply { columns.addAll(exampleHeaders) }

    @Test
    fun `Transformer calls output handler`() {
        var result = ""
        val handler = { _: Map<String, Any> -> result = "foo" }
        val emptyConfig = TransformationConfig()
        val transformer = Transformer(emptyConfig, handler)

        val input = arrayOf<String>()
        transformer.transform(input)
        assertThat(result).isEqualTo("foo")
    }

    @Test
    fun `Unknown field reports error`(): Unit = TODO("implement")

    @Test
    fun `Wrong field type reports error`(): Unit = TODO("implement")

    @Test
    fun `Unknown column index reports error`(): Unit = TODO("implement")

    @Test
    fun `Transformer can use static fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = TransformationConfig()

        config.add(StaticStringValueField("foo", "bar"))
        config.add(StaticStringValueField("baz", "qux"))
        val transformer = Transformer(config, handler)

        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo("bar")
        assertThat(resultRecord["baz"]).isEqualTo("qux")
    }

    @Test
    fun `Transformer can use date fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = exampleConfig()
        val field = DateField("foo")
        field.columns.add("Year")
        field.columns.add("Month")
        field.columns.add("Day")
        config.add(field)
        val transformer = Transformer(config, handler)

        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo(LocalDate.of(2018, 1, 1))
    }

    @Test
    fun `Transformer can use formatted date fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = TransformationConfig()
        config.columns.add("Date")

        val field = FormattedDateField("foo", "yyyy-MM-dd")
        field.columns.add("Date")
        config.add(field)
        val transformer = Transformer(config, handler)

        transformer.transform(arrayOf("2018-12-31"))

        assertThat(resultRecord["foo"]).isEqualTo(LocalDate.of(2018, 12, 31))
    }

    @Test
    fun `Transformer can use integer fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = TransformationConfig()
        config.columns.add("Integer")

        val field = IntegerField("foo")
        field.columns.add("Integer")
        config.add(field)
        val transformer = Transformer(config, handler)

        transformer.transform(arrayOf("42"))

        assertThat(resultRecord["foo"]).isEqualTo(42)
    }

    @Test
    fun `Transformer can use decimal fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = TransformationConfig()
        config.columns.add("Decimal")

        val field = DecimalField("foo", "#,##0.0#")
        field.columns.add("Decimal")
        config.add(field)

        val transformer = Transformer(config, handler)
        transformer.transform(arrayOf("5,250.50"))

        assertThat(resultRecord["foo"] as BigDecimal).isEqualByComparingTo(BigDecimal(5250.50))
    }

    @Test
    fun `Transformer can use simple string fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = exampleConfig()
        val field = StringField("foo")
        field.columns.add("Product Name")
        config.add(field)

        val transformer = Transformer(config, handler)
        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo("Arugola")
    }

    @Test
    fun `Transformer can prefix string fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = exampleConfig()
        val field = StringField("foo","Name: ")
        field.columns.add("Product Name")
        config.add(field)

        val transformer = Transformer(config, handler)
        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo("Name: Arugola")
    }

    @Test
    fun `Transformer can suffix string fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = exampleConfig()
        val field = StringField("foo",""," as name")
        field.columns.add("Product Name")
        config.add(field)

        val transformer = Transformer(config, handler)
        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo("Arugola as name")
    }

    @Test
    fun `Transformer can concat string fields`() {
        var resultRecord: Map<String, Any> = mapOf()

        val handler = { r: Map<String, Any> -> resultRecord = r }

        val config = exampleConfig()
        val field = StringField("foo","-> "," <-", " - ")
        field.columns.add("Product Number")
        field.columns.add("Product Name")
        field.columns.add("Extra Col1")
        config.add(field)

        val transformer = Transformer(config, handler)
        transformer.transform(exampleInput)

        assertThat(resultRecord["foo"]).isEqualTo("-> P-10001 - Arugola - Lorem <-")
    }

    //private fun newListener(holder: Map<String, Any>) =   { r: Map<String, Any> -> holder = r }
}
