package net.wolfgangwerner.miniwrangler.model.record

class StringField(
        name: String,
        val prefix: String = "",
        val suffix: String = "",
        val glue: String = "") : RecordField<String>(name) {

    override fun unmarshal(vararg data: String): String {
        // TODO validate
        return data.joinToString(glue, prefix, suffix)
    }
}
