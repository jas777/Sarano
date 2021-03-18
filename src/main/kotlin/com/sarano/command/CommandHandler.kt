package com.sarano.command

import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CommandHandler(val sarano: Sarano) : ListenerAdapter() {

    val commands: List<Command> = ArrayList()

    init {
        sarano.logger.info { "Command handler registered successfully" }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

        val author: User = event.author
        val guild: Guild = event.guild

        val prefix: String = sarano.configuration.prefix

        if (!event.message.contentDisplay.startsWith(prefix, ignoreCase = true)) return

        val messageWithoutPrefix: List<String> = event.message.contentDisplay.split(prefix)[1].split(" ")

        val command = messageWithoutPrefix[0]

        val args = messageWithoutPrefix.subList(1, messageWithoutPrefix.size)

        if (sarano.debug) {
            event.channel.sendMessage("Command: $command\n\nArguments: ${args.joinToString(", ")}\n\n" +
                    "Shard: ${event.jda.shardInfo.shardId}").queue()
        }

    }

}