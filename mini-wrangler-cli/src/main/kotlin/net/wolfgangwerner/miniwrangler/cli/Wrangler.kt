@file:Suppress("EXPERIMENTAL_API_USAGE")

package net.wolfgangwerner.miniwrangler.cli

import net.wolfgangwerner.miniwrangler.transformer.Transformer
import picocli.CommandLine
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable


@CommandLine.Command(name = "mini-wrangle-cli", version = ["0.0.1"],
    mixinStandardHelpOptions = true,
    description = ["Crisp Mini Wrangler - CLI Demo"])
class Wrangler : Callable<Int> {

    @Option(names = ["-c", "--config"], paramLabel = "<configuration file>",
        required = true,
        description = ["Configuration file, see README for syntax"])
    private var config: String = ""

    @Option(names = ["-i", "--input", "--csv"], paramLabel = "<CSV file>",
        required = true,
        description = ["CSV file to process"])
    private var input: String = ""

    @Option(names = ["-a", "--async"],
        description = ["Process the file in an asynchronous fashion"])
    private var async: Boolean = false


    override fun call(): Int {
        val configFile = File(config)
        val inputFile = File(input)

        val t = Transformer(
            configFile,
            { r: Map<String, Any> ->
                println(r.values.joinToString(
                    separator = "','",
                    prefix = "['",
                    postfix = "']",
                    transform = {
                        "${it.toString()} (${it.javaClass.name})"
                    }))
            },
            { row: Array<String>, exception: Exception ->
                println("ERROR: ${exception.message} in ${row.joinToString(", ", "[", "]")}")
            })

        t.transform(inputFile, !async)

        return 1
    }
}

fun main(args: Array<String>) = System.exit(CommandLine(Wrangler()).execute(*args))

