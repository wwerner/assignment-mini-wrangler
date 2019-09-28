package net.wolfgangwerner.miniwrangler.model.config

import net.wolfgangwerner.miniwrangler.model.record.RecordField
import org.simpleflatmapper.csv.CsvParser
import java.io.File

class TransformationConfig {
    internal val columns: MutableList<String> = mutableListOf<String>()
    internal val recordFields: MutableList<RecordField<Any>> = mutableListOf<RecordField<Any>>()
    private val recordFieldIndex: Map<String, RecordField<Any>> by lazy {
        recordFields.associateBy { it.name }
    }


    public fun columnIndex(name: String) = columns.indexOf(name)

    public fun add(field: RecordField<Any>) = recordFields.add(field)
    public fun add(field: RecordField<Any>, vararg colRefs: String) {
        field.columnRefs.addAll(colRefs)
        recordFields.add(field)

    }

    public fun field(name: String): RecordField<Any> = recordFieldIndex[name] ?: error("Invalid field $name requested")

    public fun ensureIsValid() {
        val errorMessage = recordFields
            .filter { !columns.containsAll(it.columnRefs) }
            .map { Pair(it.name, it.columnRefs.subtract(columns)) }
            .joinToString(separator = "\n\t", transform = {
                it.first + ' ' + it.second.joinToString(", ", "[", "]")
            })

        check(errorMessage.isEmpty()) { "Invalid columns in field configuration:\n\t$errorMessage" }
    }

    fun ensureCsvMatches(csvFile: File) {
        val headers: Array<String> = CsvParser.iterator(csvFile).next()
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
