package net.wolfgangwerner.miniwrangler.model.config

import net.wolfgangwerner.miniwrangler.model.record.RecordField

class TransformationConfig : ConfigElement() {
    internal val columns: MutableList<String> = mutableListOf<String>()
    internal val recordFields: MutableList<RecordField<Any>> = mutableListOf<RecordField<Any>>()


    public fun columnIndex(name: String) = columns.indexOf(name)

    public fun add(field: RecordField<Any>) = recordFields.add(field)
    public fun field(name: String) = recordFields.first { rf -> rf.name == name }  // TODO: Optimize?

    public fun ensureIsValid() {
        val invalidBackingColumns = recordFields
            .flatMap { it.columns }
            .filter { !columns.contains(it) }
            .toList()

        // TODO: Add field name to exception
        if (invalidBackingColumns.isNotEmpty()) throw IllegalArgumentException("Non-existing columns referenced in field definition: $invalidBackingColumns")
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
