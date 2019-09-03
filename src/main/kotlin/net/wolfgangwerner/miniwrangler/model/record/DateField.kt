package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateField(private val inputFormat: String) : Field<LocalDate>() {
    val formatter = DateTimeFormatter.ofPattern(inputFormat)

    override fun unmarshal(data: String) {
        this.value = LocalDate.parse(data, formatter)
    }

}