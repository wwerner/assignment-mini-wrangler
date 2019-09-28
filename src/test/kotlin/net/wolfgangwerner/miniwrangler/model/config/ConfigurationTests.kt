package test.kotlin.net.wolfgangwerner.miniwrangler.model.config


import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfigDsl
import net.wolfgangwerner.miniwrangler.model.config.wrangler
import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.StringField
import net.wolfgangwerner.miniwrangler.transformer.Transformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigurationTests {
    private val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts")!!

    @Test
    fun `DSL string can be evaluated`() {
        val configDsl = scriptEngine.eval(
            """
                import net.wolfgangwerner.miniwrangler.model.config.*
                wrangler {}    
            """.trimIndent()
        ) as TransformationConfigDsl

        assertThat(configDsl.config).isEqualTo(TransformationConfig())
    }

    @Test
    fun `transformation DSL supports input columns`() {
        val expected = TransformationConfig()
        expected.columns.addAll(arrayOf("foo", "bar", "baz"))

        val configDsl = wrangler {
            input {
                column("foo")
                column("bar")
                column("baz")
            }
        }

        assertThat(configDsl.config).isEqualTo(expected)
    }

    @Test
    fun `configuration can be validated`() {
        val configDsl = wrangler {
            input {
                column("foo")
                column("bar")
                column("baz")
            }
            record { }
        }
    }

    @Test
    fun `invalid configuration is detected`() {
        val config = TransformationConfig().apply {
            columns.addAll(arrayOf("foo", "bar"))
            recordFields.add(IntegerField("F1").apply { columnRefs.add("foo") })
            recordFields.add(StringField("F2").apply { columnRefs.add("bar") })
        }
        assertDoesNotThrow { config.ensureIsValid() }

        config.recordFields.add(StringField("F3").apply { columnRefs.addAll(arrayOf("baz","qux")) })
        config.recordFields.add(StringField("F4").apply { columnRefs.addAll(arrayOf("bar","baz")) })
        assertThrows<IllegalStateException> {
            try {
                config.ensureIsValid()
            } catch (e:Exception) {
                System.err.println(e)
                throw e
            }
        }
    }

    @Test
    fun `configuration not matching CSV is reported`() {
        val csv = File.createTempFile("test", ".csv")
        csv.writeText("foo,baz\n1,2")

        val config = TransformationConfig().apply {
            columns.addAll(arrayOf("foo", "bar"))
            recordFields.add(IntegerField("F1").apply { columnRefs.add("foo") })
            recordFields.add(StringField("F2").apply { columnRefs.add("bar") })
        }
        assertDoesNotThrow { config.ensureIsValid() }

        val transformer = Transformer(config, {}, { _, _ -> })
        assertThrows<IllegalArgumentException> {
            transformer.transform(csv)
        }

    }

    @Test
    fun `transformation DSL supports output records`() {
        val expected = TransformationConfig()
        expected.add(IntegerField("qux"))

        val configDsl = wrangler {
            record {
                field("qux").integerFrom("qux")

            }
        }

        assertThat(configDsl.config.field("qux")).isInstanceOf(IntegerField::class.java)
    }

}
