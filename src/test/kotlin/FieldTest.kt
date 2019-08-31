package test.kotlin

import net.wolfgangwerner.miniwrangler.DateField
import net.wolfgangwerner.miniwrangler.DecimalField
import net.wolfgangwerner.miniwrangler.IntegerField
import net.wolfgangwerner.miniwrangler.StringField
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDate


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FieldTest {
    @Test
    fun `String field can be parsed`() {
        val input = "Tapioca"
        val field = StringField()
        val parsed = field.unmarshal(input)
        assertThat(field.value()).isEqualTo(input)
    }

    @Test
    fun `Integer field can be parsed`() {
        val input = "42"
        val field = IntegerField()
        val parsed = field.unmarshal(input)
        assertThat(field.value()).isEqualTo(Integer.valueOf(42))
    }
    @Test
    fun `Date field can be parsed`() {
        val field = DateField("yyyy-MM-dd")
        val parsed = field.unmarshal("2019-08-31")
        assertThat(field.value()).isEqualTo(LocalDate.of(2019,8,31))
    }

    @Test
    fun `Decimal field can be parsed`() {
        val field = DecimalField("#,##0.0#")
        val parsed = field.unmarshal("5,250.50")
        assertThat(field.value()).isEqualByComparingTo(BigDecimal(5250.50))
    }
}