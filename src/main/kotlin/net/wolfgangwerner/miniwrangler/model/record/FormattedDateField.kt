package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FormattedDateField(name: String, private val inputFormat: String) : RecordField<LocalDate>(name) {

    val formatter = DateTimeFormatter.ofPattern(inputFormat)

    override fun unmarshal(vararg data: String): LocalDate {
        // TODO: validate
        return LocalDate.parse(data[0], formatter)
    }


}
