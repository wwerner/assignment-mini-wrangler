package net.wolfgangwerner.miniwrangler.model.config

import net.wolfgangwerner.miniwrangler.model.record.RecordField
import java.io.File

class TransformationConfig {
    internal val columns: MutableList<String> = mutableListOf<String>()
    internal val recordFields: MutableList<RecordField<Any>> = mutableListOf<RecordField<Any>>()


    public fun columnIndex(name: String) = columns.indexOf(name)

    public fun add(field: RecordField<Any>) = recordFields.add(field)
    public fun field(name: String) = recordFields.first { rf -> rf.name == name }  // TODO: Optimize?

    public fun ensureIsValid() {
        val invalidBackingColumns = recordFields
            .flatMap { it.columns }
            .filter { backingCol -> !columns.contains(backingCol) }
            .toList()

        // TODO: Add field name to exception
        check(invalidBackingColumns.isEmpty()) { "Non-existing columns referenced in field definition: $invalidBackingColumns" }
    }

    fun ensureCsvMatches(csvFile: File) {
        val headers = csvFile.readLines().first().split(",") // TODO Use the csv parser here for robustness
        val invalidColumns = columns.filter { !headers.contains(it) }

        require(invalidColumns.isEmpty()) { "Configuration contains columns not present in file: $invalidColumns" }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransformationConfig

        if (columns != other.columns) return false
        if (recordFields != other.recordFields) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columns.hashCode()
        result = 31 * result + recordFields.hashCode()
        return result
    }


}
