package com.sarano.command.argument

import net.dv8tion.jda.api.entities.Command
import java.util.*

data class CommandArgument(val name: String, val description: String, val type: Command.OptionType,
                           val optional: Boolean, val length: Int? = null, val choices: Array<String>? = emptyArray()) {

    // ----- IDEA SUGGESTED CODE START -----

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandArgument

        if (name != other.name) return false
        if (description != other.description) return false
        if (type != other.type) return false
        if (optional != other.optional) return false
        if (length != other.length) return false
        if (choices != null) {
            if (other.choices == null) return false
            if (!choices.contentEquals(other.choices)) return false
        } else if (other.choices != null) return false

        return true

    }

    override fun hashCode(): Int {

        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + optional.hashCode()
        result = 31 * result + (length ?: 0)
        result = 31 * result + (choices?.contentHashCode() ?: 0)
        return result

    }

    // ----- IDEA SUGGESTED CODE END -----

}

data class ParsedArgument <T> (val argument: CommandArgument, val result: Optional<T>)