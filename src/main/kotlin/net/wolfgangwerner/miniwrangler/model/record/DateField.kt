package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateField(name: String, private val inputFormat: String) : RecordField<LocalDate>(name) {

    val formatter = DateTimeFormatter.ofPattern(inputFormat)

    override fun unmarshal(vararg data: String) = LocalDate.parse(data[0], formatter)

    fun unmarshal(year: String, month: String, dayOfMonth: String) = LocalDate.of(
            Integer.parseInt(year),
            Integer.parseInt(month),
            Integer.parseInt(dayOfMonth)
    )

}
