package test.kotlin.net.wolfgangwerner.miniwrangler.model.config


import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
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
        ).isOfAnyClassIn(TransformationConfig::class.java)
    }

    @Test
    fun `DSL string can be evaluated`() {
        assertThat(
                TransformationConfig()
        ).isEqualTo(
                scriptEngine.eval(
                        """
                import net.wolfgangwerner.miniwrangler.model.config.*
                transformation {}    
            """.trimIndent()
                )
        )
    }

}
