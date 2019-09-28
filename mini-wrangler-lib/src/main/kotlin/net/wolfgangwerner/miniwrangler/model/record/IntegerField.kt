package net.wolfgangwerner.miniwrangler.model.record

class IntegerField(name: String) : RecordField<Int>(name) {

    override fun unmarshal(vararg data: String): Int {
        check(data.size == 1)
        val value = data[0]
        return Integer.parseInt(value)
    }
}
