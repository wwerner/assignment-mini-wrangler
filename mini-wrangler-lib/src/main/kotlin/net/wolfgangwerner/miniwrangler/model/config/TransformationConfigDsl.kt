package net.wolfgangwerner.miniwrangler.model.config

import net.wolfgangwerner.miniwrangler.model.record.*

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

    fun input(init: Input.() -> Unit) = initElement(Input(config), init)
    fun record(init: Record.() -> Unit) = initElement(Record(config), init)
    fun config() = config
}

class Input(private val config: TransformationConfig) : ConfigElement() {
    public fun column(name: String) {
        config.columns.add(name)
    }
}

class Record(private val config: TransformationConfig) : ConfigElement() {
    fun field(name: String) = Field(config, name)
}

class Field(private val config: TransformationConfig, private val name: String) {
    fun dateFrom(year: String, month: String, dayOfMonth: String) =
        config.add(DateField(name), year, month, dayOfMonth)

    fun decimalFrom(column: String, pattern: String) =
        config.add(DecimalField(name, pattern), column)

    fun formattedDateFrom(column: String, pattern: String) =
        config.add(FormattedDateField(name, pattern), column)

    fun integerFrom(column: String) =
        config.add(IntegerField(name), column)

    fun staticStringFrom(value: String) =
        config.add(StaticStringValueField(name, value))

    fun stringFrom(column: String) =
        config.add(StringField(name), column)

    fun productNameFrom(column: String) =
        config.add(ProductNameField(name), column)

    fun concatenationFrom(separator: String = "", prefix: String = "", suffix: String = "", vararg columns: String) =
        config.add(StringField(prefix, suffix, separator, name), *columns)

}

public fun wrangler(init: TransformationConfigDsl.() -> Unit): TransformationConfigDsl {
    val transformation = TransformationConfigDsl()
    transformation.init()
    return transformation
}
