package test.kotlin.net.wolfgangwerner.miniwrangler.transformer

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
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
        val handler = fun(r: Record) {
            result = "foo"
        }
        val emptyConfig = TransformationConfig()
        val transformer = Transformer(emptyConfig, handler)

        val input = arrayOf<String>()
        transformer.transform(input)
        assertThat(result).isEqualTo("foo")
    }

}
