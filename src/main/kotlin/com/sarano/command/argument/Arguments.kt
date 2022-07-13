package com.sarano.command.argument

import com.sarano.command.Command
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionType

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST")
class Arguments {

    var parsedArguments: HashMap<String, ParsedArgument<Any>> = HashMap()

    constructor (jda: JDA, command: Command, args: List<String>) {

        // Number of arguments in the message
        // val numOfArgs: Int = args.size

        // if (numOfArgs >= command.arguments.filter { !it.optional }.size) {

        // Number of arguments in the command
        val numOfCommandArgs: Int = command.arguments.size

        // Current index of argument from the args array
        var indexedArgument = 0

        for (index in 0 until numOfCommandArgs) {

            if (indexedArgument >= args.size) break

            val commandArgument: CommandArgument = command.arguments[index]
            val parsedResult: Any?

            if (commandArgument.length == null) {

                parsedResult =
                    translateOption(commandArgument.type).parseMethod(jda, args[indexedArgument])

                if (parsedResult != null) {
                    if (commandArgument.choices.size > 0 && commandArgument.choices.none { it.value == parsedResult }) {
                        break
                    }

                    indexedArgument++
                    parsedArguments[commandArgument.name] = ParsedArgument(commandArgument, parsedResult)
                } else if (commandArgument.optional) {
                    continue
                }

            } else {

                if (commandArgument.type != OptionType.STRING) error("Length only applicable to string!")

                var matchingArguments = 0
                val resultList: MutableList<Any> = ArrayList()

                for (argIndex in 0 until (if (commandArgument.length!! == -1) args.size - indexedArgument else
                    commandArgument.length)!!) {

                    if (args.size <= indexedArgument + argIndex) break

                    val result = translateOption(commandArgument.type)
                        .parseMethod(jda, args[indexedArgument + argIndex])

                    result?.let {
                        resultList.add(result)
                        matchingArguments++
                    }

                    if (result == null) break
                }

                if (commandArgument.choices.size > 0 &&
                    commandArgument.choices.none { it.value == resultList.joinToString(" ") }
                ) break


                indexedArgument += matchingArguments
                parsedArguments[commandArgument.name] = ParsedArgument(commandArgument, resultList)
            }
        }
    }

    constructor(parsedArguments: HashMap<String, ParsedArgument<Any>>) {
        this.parsedArguments = parsedArguments
    }

    private fun translateOption(optionType: OptionType): ArgumentMethods {
        return when (optionType) {
            OptionType.STRING -> ArgumentMethods.STRING
            OptionType.INTEGER -> ArgumentMethods.INTEGER
            OptionType.BOOLEAN -> ArgumentMethods.BOOLEAN
            OptionType.USER -> ArgumentMethods.USER
            else -> error("Invalid option!")
        }
    }

    operator fun get(argument: String): Any {
        return parsedArguments[argument]?.result ?: error("Argument not present!")
    }

    fun <T> optional(argument: String): T? = parsedArguments[argument]?.result as T?

    fun string(argument: String): String = this[argument] as String

    fun stringList(argument: String): List<String> = this[argument] as List<String>

    fun integer(argument: String): Int = (this[argument] as Long).toInt()

    fun long(argument: String): Long = this[argument] as Long

    fun boolean(argument: String): Boolean = this[argument] as Boolean
}

enum class ArgumentMethods constructor(val parseMethod: (jda: JDA, rawArgument: String) -> Any?) {
    // UNKNOWN(-1), SUB_COMMAND(1), SUB_COMMAND_GROUP(2), STRING(3, true), INTEGER(4, false), BOOLEAN(5), USER(6), CHANNEL(7), ROLE(8);

    STRING(
        fun(_: JDA, rawArgument: String): String {
            return rawArgument
        }
    ),

    INTEGER(
        fun(_: JDA, rawArgument: String): Long? {
            return if (rawArgument.toLongOrNull() == null) null else rawArgument.toLong()
        }
    ),

    BOOLEAN(
        fun(_: JDA, rawArgument: String): Boolean {
            return rawArgument.equals("true", ignoreCase = true)
        }
    ),

    USER(
        fun(jda: JDA, rawArgument: String): User? {
            return jda.getUserById(rawArgument) ?: (jda.getUserByTag(rawArgument) ?: jda.getUsersByName(
                rawArgument,
                true
            ).first())
        }
    )
}
