package test.kotlin.net.wolfgangwerner.miniwrangler.model.config


import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.config.TransformationConfigDsl
import net.wolfgangwerner.miniwrangler.model.config.transformation
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


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConfigurationTests {
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
    fun `configuration can be validated`() {
        val configDsl = transformation {
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
            recordFields.add(IntegerField("F1").apply { columns.add("foo") })
            recordFields.add(StringField("F2").apply { columns.add("bar") })
        }
        assertDoesNotThrow { config.ensureIsValid() }

        config.recordFields.add(StringField("F3").apply { columns.add("baz") })
        assertThrows<IllegalStateException> {
            config.ensureIsValid()
        }
    }

    @Test
    fun `configuration not matching CSV is reported`() {
        val csv = File.createTempFile("test", ".csv")
        csv.writeText("foo,baz\n1,2")

        val config = TransformationConfig().apply {
            columns.addAll(arrayOf("foo", "bar"))
            recordFields.add(IntegerField("F1").apply { columns.add("foo") })
            recordFields.add(StringField("F2").apply { columns.add("bar") })
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

        val configDsl = transformation {
            record {
                intField("qux") from "qux"

            }
        }

        assertThat(configDsl.config.field("qux")).isInstanceOf(IntegerField::class.java)
    }

}
