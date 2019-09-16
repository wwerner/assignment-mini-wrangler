package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.Record
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransformerTests {
    @Test
    fun `Transformer calls output handler`() {
        var result = ""
        val handler = { _: Record -> result = "foo" }
        val emptyConfig = TransformationConfig()
        val transformer = Transformer(emptyConfig, handler)

        val input = arrayOf<String>()
        transformer.transform(input)
        assertThat(result).isEqualTo("foo")
    }

    @Test fun `Unknown field reports error`(): Unit = TODO("implement")
    @Test fun `Wrong field type reports error`(): Unit = TODO("implement")
    @Test fun `Unknown column index reports error`(): Unit = TODO("implement")

    @Test
    fun `Transformer can read row`() {
        var resultRecord: Record? = null

        val input = arrayOf("1000", "2018", "1", "1", "P-10001", "Arugola", "5,250.50", "Lorem,Ipsum", "")
        val handler = { r: Record -> resultRecord = r }

        val config = TransformationConfig()
        config.columns.add("Order Number")
        val field =IntegerField("OrderID")
        field.columns.add(0,"Order Number")
        config.add(field)
        val transformer = Transformer(config, handler)

        transformer.transform(input)

        val result : Int = resultRecord!!.value("OrderID") as Int
        assertThat(result).isEqualTo(1000)
    }

}
