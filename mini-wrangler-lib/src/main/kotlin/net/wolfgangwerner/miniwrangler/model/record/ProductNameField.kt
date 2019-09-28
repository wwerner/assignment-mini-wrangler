package net.wolfgangwerner.miniwrangler.model.record

class ProductNameField(name: String) : RecordField<String>(name) {
    override fun unmarshal(vararg data: String): String {
        check(data.size == 1)
        return data[0].split(' ')
            .joinToString(" ") { it.capitalize() }
    }
}
