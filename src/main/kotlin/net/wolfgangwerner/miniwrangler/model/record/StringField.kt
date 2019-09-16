package net.wolfgangwerner.miniwrangler.model.record

class StringField(name: String) : RecordField<String>(name) {
    override fun unmarshal(vararg data: String) = unmarshal(data[0])
    fun unmarshal(data: String) = data
}
