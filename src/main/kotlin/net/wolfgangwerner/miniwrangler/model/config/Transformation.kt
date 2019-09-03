package net.wolfgangwerner.miniwrangler.model.config

@DslMarker
annotation class ConfigElementMarker

@ConfigElementMarker
abstract class ConfigElement {
    protected fun <T : ConfigElement> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        return element
    }
}

class Transformation : ConfigElement() {

    public fun input(init: Input.() -> Unit) = initElement(Input(), init)
    public fun records(init: Records.() -> Unit) = initElement(Records(), init)
    public fun output(init: Output.() -> Unit) = initElement(Output(), init)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

class Input() : ConfigElement() {
    public fun column(name: String) = Column(name)

}

class Column(name: String) : ConfigElement() {}

class Records() : ConfigElement()
class Output() : ConfigElement() {
    public fun stdout(init: StdOut.() -> Unit) = initElement(StdOut(), init)
    public fun stderr(init: StdErr.() -> Unit) = initElement(StdErr(), init)
    public fun file(file: String) = File(file)
}

abstract class OutputChannel() : ConfigElement()
class StdOut() : OutputChannel() {}
class StdErr() : OutputChannel() {}
class File(path: String) : OutputChannel() {}

public fun transformation(init: Transformation.() -> Unit): Transformation {
    val transformation = Transformation()
    transformation.init()
    return transformation
}