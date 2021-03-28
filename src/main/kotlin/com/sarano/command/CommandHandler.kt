package com.sarano.command

import com.sarano.command.argument.Arguments
import com.sarano.main.Sarano
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction
import net.dv8tion.jda.api.entities.Command.OptionType
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.util.*
import kotlin.collections.ArrayList

class CommandHandler(val sarano: Sarano) : ListenerAdapter() {

    val commands: MutableList<Command> = ArrayList()

    lateinit var slashRegistry: CommandUpdateAction

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

            if (it.ownerOnly && !sarano.configuration.developers.contains(event.author.id)) {
                return@getCommand
            }

            if (sarano.debug && event.message.contentDisplay.endsWith("-d")) {
                event.channel.sendMessage(
                    "Command: ${finalCommand.name}\n\nArguments: ${args.joinToString(", ")}\n\n" +
                            "Shard: ${event.jda.shardInfo.shardId}"
                ).queue()
            }

            val arguments = Arguments(it, args)

            if (arguments.parsedArguments.size < it.arguments.filter { arg -> !arg.optional }.size) {
                event.channel.sendMessage("Invalid arguments [debug]").queue()
                return@getCommand
            }

            event.member?.let { member -> finalCommand.execute(member, event.channel, event.message, event.guild, args) }

        }

    }

    override fun onSlashCommand(event: SlashCommandEvent) {

        getCommand(event.name, sarano.configuration.developers.contains(event.user.id)) { it ->
            try {
                it.executeSlash(event)
            } catch (error: NotImplementedError) {
                sarano.logger.error { error.message }
            }
        }

    }

    override fun onReady(event: ReadyEvent) {
        slashRegistry = sarano.client.shards.first().updateCommands()
        registerSlash()
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

    private fun registerSlash() {

        for (command in commands.filter { it.canSlash }) {

            val slashCommand = CommandUpdateAction.CommandData(command.name, command.description)

            for (argument in command.arguments.sortedBy { it.optional }) {
                slashCommand.addOption(
                    CommandUpdateAction.OptionData(argument.type, argument.name, argument.description)
                        .setRequired(!argument.optional)
                )
            }

            slashRegistry.addCommands(
                slashCommand
            )
        }

        for (guild in sarano.client.guilds) {
            guild.updateCommands().queue()
        }

        slashRegistry.queue()

        sarano.logger.debug { "Sent slash commands update" }

    }

}
