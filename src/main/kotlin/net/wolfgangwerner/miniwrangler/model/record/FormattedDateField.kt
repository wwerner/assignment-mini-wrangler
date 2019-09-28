package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FormattedDateField(name: String, private val inputFormat: String) : RecordField<LocalDate>(name) {

    private val formatter = DateTimeFormatter.ofPattern(inputFormat)

    override fun unmarshal(vararg data: String): LocalDate {
        check(data.size == 1)
        return LocalDate.parse(data[0], formatter)
    }


}
