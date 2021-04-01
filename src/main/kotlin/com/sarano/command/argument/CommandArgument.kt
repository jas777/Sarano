package com.sarano.command.argument

import net.dv8tion.jda.api.entities.Command
import java.util.*
import kotlin.collections.HashMap

data class CommandArgument(val name: String, val description: String, val type: Command.OptionType,
                           val optional: Boolean, val length: Int? = null, val choices: HashMap<String, Any> = hashMapOf()
)

data class ParsedArgument <T> (val argument: CommandArgument, val result: Optional<T>)