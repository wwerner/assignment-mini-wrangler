package net.wolfgangwerner.miniwrangler.model.record

import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalField(name: String, val decimalFormat: String) : RecordField<BigDecimal>(name) {
    val formatter = DecimalFormat(decimalFormat)

    init {
        formatter.setParseBigDecimal(true)
    }

    override fun unmarshal(vararg data: String): BigDecimal {
        // TODO: validate
        val value = data[0]
        return formatter.parse(value) as BigDecimal
    }
}
