package test.kotlin.net.wolfgangwerner.miniwrangler.model.config


import net.wolfgangwerner.miniwrangler.model.config.Transformation
import net.wolfgangwerner.miniwrangler.model.config.transformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigDslTest {
    private val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")!!

    @Test
    fun `transformation DSL is available`() {
        assertThat(
            transformation {
                input {
                    column("foo")
                    column("bar")
                    column("baz")
                }

                output {
                    file("foo.bar")
                }
            }
        ).isOfAnyClassIn(Transformation::class.java)
    }

    @Test
    fun `transformation element can be parsed`() {
        testDsl(
            Transformation(), """
            import net.wolfgangwerner.miniwrangler.model.config.*
            transformation {}    
        """.trimIndent()
        )
    }

    private fun testDsl(expected: Transformation, dsl: String) {

        assertThat(expected).isEqualTo(scriptEngine.eval(dsl))
    }
}