package test.kotlin.net.wolfgangwerner.miniwrangler.model.record

import net.wolfgangwerner.miniwrangler.model.record.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDate


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecordFieldTest {
    @Test
    fun `String field can be parsed`() {
        val input = "Tapioca"
        val field = StringField("f")
        assertThat(field.unmarshal(input)).isEqualTo(input)
    }

    @Test
    fun `Integer field can be parsed`() {
        val input = "42"
        val field = IntegerField("f")
        field.unmarshal(input)
        assertThat(field.unmarshal(input)).isEqualTo(Integer.valueOf(42))
    }

    @Test
    fun `Date field can be parsed from pattern`() {
        val field = FormattedDateField("f", "yyyy-MM-dd")
        assertThat(field.unmarshal("2019-08-31")).isEqualTo(LocalDate.of(2019, 8, 31))
    }

    @Test
    fun `Date field can be parsed from string args`() {
        val field = DateField("f")
        assertThat(field.unmarshal("2019", "08", "31")).isEqualTo(LocalDate.of(2019, 8, 31))
    }

    @Test
    fun `Decimal field can be parsed`() {
        val field = DecimalField("f","#,##0.0#")
        assertThat(field.unmarshal("5,250.50")).isEqualByComparingTo(BigDecimal(5250.50))
    }
}
