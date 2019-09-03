package net.wolfgangwerner.miniwrangler.model.record

abstract class Field<T : Any> {
    protected lateinit var value: T

    abstract fun unmarshal(data: String)

    open fun marshal(data: String): String {
        return value.toString()
    }

    public fun value() = value
}