package test.kotlin.net.wolfgangwerner.miniwrangler.model.record

import net.wolfgangwerner.miniwrangler.model.record.DateField
import net.wolfgangwerner.miniwrangler.model.record.DecimalField
import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.StringField
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
        field.unmarshal(input)
        assertThat(field.value()).isEqualTo(input)
    }

    @Test
    fun `Integer field can be parsed`() {
        val input = "42"
        val field = IntegerField()
        field.unmarshal(input)
        assertThat(field.value()).isEqualTo(Integer.valueOf(42))
    }
    @Test
    fun `Date field can be parsed`() {
        val field = DateField("yyyy-MM-dd")
        field.unmarshal("2019-08-31")
        assertThat(field.value()).isEqualTo(LocalDate.of(2019,8,31))
    }

    @Test
    fun `Decimal field can be parsed`() {
        val field = DecimalField("#,##0.0#")
        field.unmarshal("5,250.50")
        assertThat(field.value()).isEqualByComparingTo(BigDecimal(5250.50))
    }
}