package net.wolfgangwerner.miniwrangler.cli

import net.wolfgangwerner.miniwrangler.transformer.Transformer

import java.io.File

fun main(args : Array<String>) {
    val config = File("")
    val t = Transformer(config, {}, { _, _ -> })
    kotlin.io.println("Hello Wrangler")
}
