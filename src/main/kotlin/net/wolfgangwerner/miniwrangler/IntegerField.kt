package net.wolfgangwerner.miniwrangler

class IntegerField : Field<Int>() {
    override fun unmarshal(data: String) {
        this.value = data.toInt()
    }

}