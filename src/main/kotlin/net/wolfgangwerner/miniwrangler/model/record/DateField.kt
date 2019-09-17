package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateField(name: String) : RecordField<LocalDate>(name) {


    override fun unmarshal(vararg data: String): LocalDate {
        // TODO: validate
        val year = data[0]
        val month = data[1]
        val dayOfMonth = data[2]
        return LocalDate.of(
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(dayOfMonth))
    }
}
