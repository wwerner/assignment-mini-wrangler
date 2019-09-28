package net.wolfgangwerner.miniwrangler.model.record

import java.math.BigDecimal
import java.text.DecimalFormat

class DecimalField(
    name: String,
    private val decimalFormat: String,
    private val groupingSeparator: Char = ',',
    private val decimalSeparator: Char = '.'
) : RecordField<BigDecimal>(name) {

    override fun unmarshal(vararg data: String): BigDecimal {
        val value = data[0]

        // This looks like it could be written more concise, but...
        // * ... DecimalFormat is not thread safe, so we create a new instance for each transformation
        // * ... if we don't set the separators, the string will be parsed with the ones from the current locale
        // * ... DecimalFormat.getDecimalFormatSymbols returns a copy, so we need to obtain it, set our values and then update the field
        val formatter = DecimalFormat(decimalFormat)
        formatter.isParseBigDecimal = true
        val symbols = formatter.decimalFormatSymbols
        symbols.groupingSeparator = groupingSeparator
        symbols.decimalSeparator = decimalSeparator
        formatter.decimalFormatSymbols = symbols
        return formatter.parse(value) as BigDecimal
    }
}
