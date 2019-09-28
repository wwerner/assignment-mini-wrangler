package net.wolfgangwerner.miniwrangler.model.record

import java.time.LocalDate
import java.time.format.DateTimeParseException

class DateField(name: String) : RecordField<LocalDate>(name) {


    override fun unmarshal(vararg data: String): LocalDate {
        val year = data[0]
        val month = data[1]
        val dayOfMonth = data[2]

        val date = try {
            LocalDate.of(
                Integer.parseInt(year),
                Integer.parseInt(month),
                Integer.parseInt(dayOfMonth)
            )
        } catch (e: NumberFormatException) {
            throw DateTimeParseException(
                data.joinToString(","),
                "At least one date column could not be parsed",
                0,
                e
            )
        }
        return date
    }
}
