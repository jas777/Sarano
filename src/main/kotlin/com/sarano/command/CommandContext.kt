package com.sarano.command

import com.sarano.command.argument.Arguments
import com.sarano.main.Sarano
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction

data class CommandContext(
    val sarano: Sarano, val sender: Member, val channel: TextChannel, val message: Message?,
    val guild: Guild, val args: Arguments, val slash: Boolean,
    val slashEvent: SlashCommandEvent?, val debug: Boolean
) {

    fun slashReply(): ReplyAction = slashEvent!!.deferReply()
    fun slashReply(content: String): ReplyAction = slashEvent!!.deferReply().setContent(content)

    fun generateButtonId(commandName: String, buttonName: String, vararg arguments: String): String
        = "${commandName}:${buttonName}:${sender.id}:${arguments.joinToString(":")}"

    fun reply(message: String) {
        if (slash) {
            slashEvent!!.hook.sendMessage(message).queue()
        } else {
            channel.sendMessage(message).queue()
        }
    }

    fun reply(message: MessageEmbed) {
        if (slash) {
            slashEvent!!.hook.sendMessage(MessageBuilder().setEmbed(message).build()).queue()
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
