package net.wolfgangwerner.miniwrangler.model.record

class StaticStringValueField(
        name: String,
        val value: String = ""
) : RecordField<String>(name) {
    override fun unmarshal(vararg data: String): String {
        return value
    }
}