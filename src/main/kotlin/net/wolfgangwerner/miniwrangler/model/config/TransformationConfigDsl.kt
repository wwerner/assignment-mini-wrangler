package net.wolfgangwerner.miniwrangler.model.config

import net.wolfgangwerner.miniwrangler.model.record.IntegerField
import net.wolfgangwerner.miniwrangler.model.record.RecordField

@DslMarker
annotation class ConfigElementMarker

@ConfigElementMarker
abstract class ConfigElement {
    protected fun <T : ConfigElement> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        return element
    }
}

class TransformationConfigDsl : ConfigElement() {

    val config: TransformationConfig = TransformationConfig()

    public fun input(init: Input.() -> Unit) = initElement(Input(config), init)
    public fun record(init: Record.() -> Unit) = initElement(Record(config), init)
    public fun output(init: Output.() -> Unit) = initElement(Output(config), init)

    public fun config() = config

}

class Input(private val config: TransformationConfig) : ConfigElement() {
    public fun column(name: String) {
        config.columns.add(name)
    }
}

class Record(private val config: TransformationConfig) : ConfigElement() {
    fun field(name: String) = Field(config, name)
    fun intField(name: String) = IntField(config, name)


}

class IntField(private val config: TransformationConfig, private val name : String) {

    init {
        config.add(IntegerField(name))
    }

    infix fun from(column: String) {
        config.field(name).columns.add(column)
    }
}

class Field(private val config: TransformationConfig, private val name : String) {
     infix fun ofType(type : RecordField<Any>) = apply {
        config.add(type)

    }
    infix fun from(column: String) {
        config.field(name).columns.add(column)
    }
}

class Output(config: TransformationConfig) : ConfigElement() {
    public fun stdout(init: StdOut.() -> Unit) = initElement(StdOut(), init)
    public fun stderr(init: StdErr.() -> Unit) = initElement(StdErr(), init)
    public fun file(file: String) = File(file)
}

abstract class OutputChannel() : ConfigElement()
class StdOut() : OutputChannel() {}
class StdErr() : OutputChannel() {}
class File(path: String) : OutputChannel() {}

public fun transformation(init: TransformationConfigDsl.() -> Unit): TransformationConfigDsl {
    val transformation = TransformationConfigDsl()
    transformation.init()
    return transformation
}
