package net.wolfgangwerner.miniwrangler.model.record

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig

abstract class RecordField<out T>(public val name: String) {
    val columns: MutableList<String> = mutableListOf()
    abstract fun unmarshal(vararg parameters: String): T

    fun unmarshal(row: Array<String>, config: TransformationConfig): T {
        val colData =
            config.field(name)
                .columns
                .map {
                    val colIdx = config.columnIndex(it)
                    if (colIdx == -1) throw IllegalArgumentException("Column '$it' not found in record '$name'")
                    else row[colIdx]
                }
                .toTypedArray()
        return unmarshal(*colData)
    }
}
