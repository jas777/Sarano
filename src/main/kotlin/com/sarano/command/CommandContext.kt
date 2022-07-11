package com.sarano.command

import com.sarano.command.argument.Arguments
import com.sarano.main.Sarano
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

data class CommandContext(
    val sarano: Sarano, val sender: Member, val channel: TextChannel, val message: Message?,
    val guild: Guild, val args: Arguments, val slash: Boolean,
    val slashEvent: SlashCommandInteractionEvent?, val debug: Boolean
) {

    fun slashReply(): ReplyCallbackAction = slashEvent!!.deferReply()
    fun slashReply(content: String): ReplyCallbackAction = slashEvent!!.deferReply().setContent(content)

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
            slashEvent!!.hook.sendMessage(MessageBuilder().setEmbeds(message).build()).queue()
        } else {
            channel.sendMessageEmbeds(message).queue()
        }
    }

    fun debug(message: String) {
        if (debug) {
            channel.sendMessage(message).queue()
        }
    }

    fun debug(message: MessageEmbed) {
        if (debug) {
            channel.sendMessageEmbeds(message).queue()
        }
    }
}
