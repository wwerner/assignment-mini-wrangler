package net.wolfgangwerner.miniwrangler.model.record

abstract class RecordField<out T>(public val name: String) {
    val columns : MutableList<String> = mutableListOf()
    abstract fun unmarshal(vararg data: String): T

}
