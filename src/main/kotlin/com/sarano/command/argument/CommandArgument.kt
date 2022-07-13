package com.sarano.command.argument

import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.collections.HashMap

data class CommandArgument(
    val name: String, val description: String, val type: OptionType,
    val optional: Boolean, var length: Int? = null, val choices: HashMap<String, Any> = hashMapOf()
) {
    init {
        if (choices.size > 0) {
            var longest = 0

            if (this.type == OptionType.STRING) {
                choices.forEach {
                    if ((it.value as String).length > longest) longest = (it.value as String).split(" ").size
                }
            }

            if (longest > 1) length = longest
        }
    }
}

data class ParsedArgument<T>(val argument: CommandArgument, val result: T)
