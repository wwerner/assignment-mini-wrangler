package net.wolfgangwerner.miniwrangler

import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalField(val decimalFormat: String) : Field<BigDecimal>() {
    val formatter = DecimalFormat(decimalFormat)

    init {
        formatter.setParseBigDecimal(true)
    }

    override fun unmarshal(data: String) {
        this.value = formatter.parse(data) as BigDecimal
    }
}