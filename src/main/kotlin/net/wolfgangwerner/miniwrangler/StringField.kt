package net.wolfgangwerner.miniwrangler

class StringField : Field<String>() {
    override fun unmarshal(data: String) {
        this.value = data
    }
}