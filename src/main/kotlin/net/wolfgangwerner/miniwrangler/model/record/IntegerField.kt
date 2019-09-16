package net.wolfgangwerner.miniwrangler.model.record

class IntegerField(name: String) : RecordField<Int>(name) {
    override fun unmarshal(vararg data: String): Int = unmarshal(data[0])
    fun unmarshal(data: String) = data.toInt()
}
