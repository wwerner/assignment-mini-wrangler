package net.wolfgangwerner.miniwrangler.transformer

import net.wolfgangwerner.miniwrangler.model.config.TransformationConfig
import net.wolfgangwerner.miniwrangler.model.record.Record

class Transformer(val config: TransformationConfig, val outputHandler: (record: Record) -> Unit) {

    public fun transform(row: Array<String>) {
        val result = Record(row, config)

        outputHandler(result)
    }



}
