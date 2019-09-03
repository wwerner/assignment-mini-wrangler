package net.wolfgangwerner.miniwrangler.model.record

class IntegerField : Field<Int>() {
    override fun unmarshal(data: String) {
        this.value = data.toInt()
    }

}