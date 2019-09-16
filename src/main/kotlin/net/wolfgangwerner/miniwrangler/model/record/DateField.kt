package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateField(private val inputFormat: String) : Field<LocalDate>() {
    val formatter = DateTimeFormatter.ofPattern(inputFormat)

    fun unmarshal(year: Int, month: Int, dayOfMonth: Int) {
        this.value = LocalDate.of(year, month, dayOfMonth)
    }

    fun unmarshal(year: String, month: String, dayOfMonth: String) {
        this.value = LocalDate.of(
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(dayOfMonth)
        )
    }

    override fun unmarshal(data: String) {
        this.value = LocalDate.parse(data, formatter)
    }

}
