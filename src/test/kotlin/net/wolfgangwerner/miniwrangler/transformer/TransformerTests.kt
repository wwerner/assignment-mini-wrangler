package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.DateField
import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.StaticStringValueField
import net.wolfgangwerner.miniwrangler.model.record.StringField
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformerTests {
    val exampleHeaders = arrayOf("Order Number", "Year", "Month", "Day", "Product Number", "Product Name", "Count", "Extra Col1", "Extra Col2", "Empty Column")
    val testFile = File("src/test/resources/orders.csv")

    private fun exampleConfig() = TransformationConfig().apply { columns.addAll(exampleHeaders) }

    @Test
    fun `Transformer transforms complete file`() {
        var resultCollection = mutableListOf<Map<String, Any>>()
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
        var resultCollection = mutableListOf<Map<String, Any>>()
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
        assertThat(resultCollection).contains(mapOf(
                "i" to 1000,
                "s" to "P-10001: Arugola",
                "d" to LocalDate.of(2018, 1, 1),
                "v" to "X"
        ))
        assertThat(resultCollection).contains(mapOf(
                "i" to 1001,
                "s" to "P-10002: Iceberg lettuce",
                "d" to LocalDate.of(2017, 12, 12),
                "v" to "X"
        ))
    }

    @Test
    fun `Transformer supports mapping columns twice`() {
        var resultCollection = mutableListOf<Map<String, Any>>()
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
}
