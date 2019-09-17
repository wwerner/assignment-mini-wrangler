package net.wolfgangwerner.miniwrangler.model.record

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig

abstract class RecordField<out T>(public val name: String) {
    val columns: MutableList<String> = mutableListOf()
    abstract fun unmarshal(vararg parameters: String): T

    fun unmarshal(row: Array<String>, config: TransformationConfig): T {
        val colData =
            config.field(name)
                .columns
                .map { col -> row[config.columnIndex(col)] }
                .toTypedArray()

        return unmarshal(*colData)
    }
}
