package net.wolfgangwerner.miniwrangler.model.record

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig

class Record(private val row: Array<String>, private val config: TransformationConfig) {

    fun value(fieldName: String): Any {
        val field = config.field(fieldName)
        return field.unmarshal(row, config)
    }

    fun value(): Map<String, Any> =
        config.recordFields
            .map { f -> f.name to value(f.name) }
            .toMap()
}
