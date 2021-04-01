package com.sarano.command.argument

import com.sarano.command.Command
import com.sarano.main.Sarano
import java.util.*

import net.dv8tion.jda.api.entities.Command.OptionType
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST")
class Arguments(val command: Command, val args: List<String>) {

    var parsedArguments: HashMap<String, ParsedArgument<*>> = HashMap()

    init {

        // Number of arguments in the message
        val numOfArgs: Int = args.size

        // if (numOfArgs >= command.arguments.filter { !it.optional }.size) {

            // Number of arguments in the command
            val numOfCommandArgs: Int = command.arguments.size

            // Current index of argument from the args array
            var indexedArgument = 0

            for (index in 0 until numOfCommandArgs) {

                if (indexedArgument >= args.size) break

                val commandArgument: CommandArgument = command.arguments[index]

                val parsedResult: Optional<*>

                if (commandArgument.length == null) {

                    parsedResult =
                        translateOption(commandArgument.type).parseMethod(command.sarano, args[indexedArgument])

                    if (parsedResult.isPresent) {

                        indexedArgument++
                        parsedArguments[commandArgument.name] = ParsedArgument(commandArgument, parsedResult)

                    } else if (parsedResult.isEmpty && commandArgument.optional) {
                        continue
                    }

                } else {

                    if (commandArgument.type != OptionType.STRING) throw Error("Length only applicable to string!")

                    var matchingArguments = 0

                    val resultList: MutableList<Any> = ArrayList()

                    for (argIndex in 0 until if(commandArgument.length == -1) args.size - indexedArgument else
                        commandArgument.length) {

                        if (args.size <= indexedArgument + argIndex) break

                        val result = translateOption(commandArgument.type)
                            .parseMethod(command.sarano, args[indexedArgument + argIndex])

                        if (result.isPresent) {
                            resultList.add(result.get())
                            matchingArguments++
                        } else break

                    }

                    indexedArgument += matchingArguments
                    parsedArguments[commandArgument.name] = ParsedArgument(commandArgument, Optional.of(resultList))

                }
            }
        // }
    }

    private fun translateOption(optionType: OptionType): ArgumentMethods {
        return when (optionType) {
            OptionType.STRING -> ArgumentMethods.STRING
            OptionType.INTEGER -> ArgumentMethods.INTEGER
            OptionType.BOOLEAN -> ArgumentMethods.BOOLEAN
            else -> throw Error("Invalid option!")
        }
    }

}

enum class ArgumentMethods constructor(val parseMethod: (sarano: Sarano, rawArgument: String) -> Optional<*>) {

    // UNKNOWN(-1), SUB_COMMAND(1), SUB_COMMAND_GROUP(2), STRING(3, true), INTEGER(4, false), BOOLEAN(5), USER(6), CHANNEL(7), ROLE(8);

    STRING(
        fun(_: Sarano, rawArgument: String): Optional<String> {
            return Optional.of(rawArgument)
        }
    ),

    INTEGER(
        fun(_: Sarano, rawArgument: String): Optional<Long> {
            return if (rawArgument.toIntOrNull() == null) Optional.empty<Long>() else Optional.of(rawArgument.toLong())
        }
    ),

    BOOLEAN(
        fun(_: Sarano, rawArgument: String): Optional<Boolean> {
            return Optional.of(rawArgument.equals("true", ignoreCase = true))
        }
    )

}