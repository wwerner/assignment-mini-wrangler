package net.wolfgangwerner.miniwrangler.model.record

class IntegerField(name: String) : RecordField<Int>(name) {

    override fun unmarshal(vararg data: String): Int {
        // TODO: validate
        val value = data[0]
        return Integer.parseInt(value)
    }
}
