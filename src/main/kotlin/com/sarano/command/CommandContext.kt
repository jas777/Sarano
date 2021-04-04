package com.sarano.command

import com.sarano.command.argument.ParsedArgument
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

data class CommandContext(
    val sender: Member, val channel: TextChannel, val message: Message?,
    val guild: Guild, val args: HashMap<String, ParsedArgument<*>>, val slash: Boolean,
    val slashEvent: SlashCommandEvent?, val debug: Boolean) {

    fun reply(message: String) {
        if (slash) {
            slashEvent?.reply(message)?.queue()
        } else {
            channel.sendMessage(message).queue()
        }
    }

    fun reply(message: MessageEmbed) {
        if (slash) {
            slashEvent?.reply(message)?.queue()
        } else {
            channel.sendMessage(message).queue()
        }
    }

    fun debug(message: String) {
        if (debug) {
            channel.sendMessage(message).queue()
        }
    }

    fun debug(message: MessageEmbed) {
        if (debug) {
            channel.sendMessage(message).queue()
        }
    }

}