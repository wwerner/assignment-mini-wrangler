package net.wolfgangwerner.miniwrangler.model.record

class StringField(
    name: String,
    private val prefix: String = "",
    private val suffix: String = "",
    private val separator: String = ""
) : RecordField<String>(name) {

    override fun unmarshal(vararg data: String): String {
        return data.joinToString(separator, prefix, suffix)
    }
}
