package net.wolfgangwerner.miniwrangler.model.record

class StringField : Field<String>() {
    override fun unmarshal(data: String) {
        this.value = data
    }
}