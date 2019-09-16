package test.kotlin.net.wolfgangwerner.miniwrangler.model.config


import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfigDsl
import net.wolfgangwerner.miniwrangler.model.config.transformation
import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.StringField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigDslTest {
    private val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")!!

    @Test
    fun `DSL string can be evaluated`() {
        val configDsl = scriptEngine.eval(
                """
                import net.wolfgangwerner.miniwrangler.model.config.*
                transformation {}    
            """.trimIndent()
        ) as TransformationConfigDsl

        assertThat(configDsl.config).isEqualTo(TransformationConfig())
    }

    @Test
    fun `transformation DSL supports input columns`() {
        val expected = TransformationConfig()
        expected.columns.addAll(arrayOf("foo", "bar", "baz"))

        val configDsl = transformation {
            input {
                column("foo")
                column("bar")
                column("baz")
            }
        }

        assertThat(configDsl.config).isEqualTo(expected)
    }

    @Test
    fun `configuration DSL is validated`() {
        TODO()
    }

    @Test
    fun `transformation DSL supports output records`() {
        val expected = TransformationConfig()
        expected.add(IntegerField("qux"))

        val configDsl = transformation {
            record {
                intField("qux") from "qux"

            }
        }

        assertThat(configDsl.config.field("qux")).isInstanceOf(IntegerField::class.java)
    }

}
