package com.sarano.command

import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import kotlin.collections.ArrayList

class CommandHandler(val sarano: Sarano) : ListenerAdapter() {

    val commands: MutableList<Command> = ArrayList()

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

        var args = messageWithoutPrefix.subList(1, messageWithoutPrefix.size)

        getCommand(command, sarano.configuration.developers.contains(author.id)) {

            val finalCommand: Command = getChild(it, args)

            args = args.drop(args.indexOf(finalCommand.name) + 1)

            sarano.logger.info { "${author.asTag} tried running ${finalCommand.name} in " +
                    "${guild.id} on shard ${event.jda.shardInfo.shardId}" }

            if (sarano.debug && event.message.contentDisplay.endsWith("-d")) {
                event.channel.sendMessage(
                    "Command: ${finalCommand.name}\n\nArguments: ${args.joinToString(", ")}\n\n" +
                            "Shard: ${event.jda.shardInfo.shardId}"
                ).queue()
            }

            event.member?.let { member -> finalCommand.execute(member, event.channel, event.message, event.guild, args) }

        }

    }

    fun getCommand(name: String, ownerOnly: Boolean = false, commandConsumer: (Command) -> Unit): Command? {

        val command: Optional<Command> = commands.stream()
            .filter { it.name.equals(name, ignoreCase = true) || it.aliases.contains(name) }
            .findFirst()

        return if (command.isEmpty) {
            null
        } else {
            commandConsumer(command.get())
            return command.get()
        }

    }

    fun getCommand(name: String, ownerOnly: Boolean = false): Command? = getCommand(name, ownerOnly) {}

    fun getChild(command: Command, args: List<String>): Command {

        if (args.isEmpty()) return command

        if (command.child.isNotEmpty() && command.child.map(Command::name).contains(args[0].toLowerCase())) {
            return getChild(command.child.first { it.name == args[0].toLowerCase() }, args.drop(1))
        }

        return command

    }

    fun registerCommands(vararg commands: Command) {

        commands.forEach {
            sarano.logger.info { "${it.name} registered" }
        }

        this.commands.addAll(commands)
    }

}
