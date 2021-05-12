package com.sarano.command

import com.sarano.command.argument.Arguments
import com.sarano.command.argument.CommandArgument
import com.sarano.command.argument.ParsedArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class CommandHandler(val sarano: Sarano) : ListenerAdapter() {

    val commands: MutableList<Command> = mutableListOf()

    lateinit var slashRegistry: CommandUpdateAction

    init {
        sarano.logger.info { "Command handler registered successfully" }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

        val author: User = event.author
        val guild: Guild = event.guild

        val prefix: String = sarano.configuration.prefix

        if (event.member == null) return

        if (!event.message.contentDisplay.startsWith(prefix, ignoreCase = true)) return

        if (!event.guild.getMember(event.jda.selfUser)?.hasPermission(event.channel, Permission.MESSAGE_WRITE)!!)
            return

        val messageWithoutPrefix: List<String> = event.message.contentDisplay.split(prefix)[1].split(" ")

        val command = messageWithoutPrefix[0]

        var args = messageWithoutPrefix.subList(1, messageWithoutPrefix.size)

        getCommand(command, sarano.configuration.developers.contains(author.id)) {

            val childResult: Pair<Int, Command> = getChild(it, args)

            val finalCommand = childResult.second

            args = args.drop(childResult.first)

            var missingBotPermissions: Array<Permission> = finalCommand.botPermissions

            missingBotPermissions = missingBotPermissions.filter { perm ->
                !event.guild.getMember(event.jda.selfUser)!!.getPermissions(event.channel).toTypedArray().contains(perm)
            }.toTypedArray()

            if (missingBotPermissions.isNotEmpty()) {

                val builder = sarano.errorEmbed()
                    .setTitle("Missing permissions!")
                    .setDescription("I need **${
                        missingBotPermissions.joinToString(", ")
                        { p -> p.getName() }
                    }** permission(s) in order to execute this command!"
                    )

                event.channel.sendMessage(builder.build()).queue()

                return@getCommand

            }

            var missingUserPermissions: Array<Permission> = finalCommand.userPermissions

            missingUserPermissions = missingUserPermissions.filter { perm ->
                !event.member!!.getPermissions(event.channel).toTypedArray().contains(perm)
            }.toTypedArray()

            if (missingUserPermissions.isNotEmpty()) {

                val builder = sarano.errorEmbed()
                    .setTitle("Missing permissions!")
                    .setDescription("You need **${
                        missingUserPermissions.joinToString(", ")
                        { p -> p.getName() }
                    }** permission(s) in order to run this command!"
                    )

                event.channel.sendMessage(builder.build()).queue()

                return@getCommand

            }

            sarano.logger.info {
                "${author.asTag} tried running ${finalCommand.name} in " +
                        "${guild.id} on shard ${event.jda.shardInfo.shardId}"
            }

            if (finalCommand.ownerOnly && !sarano.configuration.developers.contains(event.author.id)) {
                return@getCommand
            }

            var debug = false

            if (sarano.debug && event.message.contentDisplay.endsWith("-d")) {
                args = args.dropLast(1)
                debug = true
                event.channel.sendMessage(
                    sarano.warnEmbed().setTitle("Running command in debug mode").build()
                ).queue()
            }

            val arguments = Arguments(event.jda, finalCommand, args)

            if (arguments.parsedArguments.size < finalCommand.arguments.filter { arg -> !arg.optional }.size) {

                val missingArguments: List<CommandArgument> = listOf(*finalCommand.arguments.clone())
                    .filter { arg -> !arguments.parsedArguments.keys.contains(arg.name) }

                val builder = sarano.errorEmbed()
                    .setTitle("Missing arguments!")
                    .setDescription(missingArguments
                        .filter { arg -> !arg.optional }
                        .joinToString("\n") { arg -> "`${arg.name}` - ${arg.description}" })

                event.channel.sendMessage(builder.build()).queue()

                return@getCommand

            }

            val context = CommandContext(
                sarano, event.member!!, event.channel, event.message,
                event.guild, arguments, false, null, debug
            )

            event.member?.let { finalCommand.execute(context) }

        }

    }

    override fun onSlashCommand(event: SlashCommandEvent) {

        try {

            getCommand(event.name, sarano.configuration.developers.contains(event.user.id)) {

                if (!event.isFromGuild) return@getCommand

                val arguments: HashMap<String, ParsedArgument<Any>> = hashMapOf()

                for (option in event.options) {

                    val commandArgument = it.arguments.find { arg -> arg.name == option.name }

                    val result: Any = when (option.type) {

                        OptionType.STRING -> {
                            if (commandArgument?.length == null) {
                                option.asString
                            } else {
                                option.asString.split(" ")
                            }
                        }

                        OptionType.INTEGER -> option.asLong

                        OptionType.BOOLEAN -> option.asBoolean

                        OptionType.USER -> option.asUser

                        OptionType.CHANNEL -> option.asGuildChannel

                        OptionType.ROLE -> option.asRole

                        OptionType.SUB_COMMAND -> TODO()

                        OptionType.SUB_COMMAND_GROUP -> TODO()

                        OptionType.UNKNOWN -> error("Unknown option type")

                        else -> error("Unknown option type")

                    }

                    commandArgument.let { arg ->
                        arguments.put(option.name, ParsedArgument(arg!!, result))
                    }

                }

                val context = CommandContext(
                    sarano, event.member!!, event.channel as TextChannel, null,
                    event.guild!!, Arguments(arguments), true, event, sarano.debug
                )

                it.execute(context)

            }

        } catch (error: Error) {
            event.reply(sarano.errorEmbed().setDescription("${error.message}\n\nPlease contact support")
                .build()).queue()
        }

    }

    override fun onReady(event: ReadyEvent) {
        slashRegistry = sarano.client.shards.first().updateCommands()
        registerSlash()
    }

    fun getCommand(name: String, ownerOnly: Boolean = false, commandConsumer: (Command) -> Unit): Command? {

        val command: Command? = commands.find { it.name.equals(name, ignoreCase = true) || it.aliases.contains(name) }

        command?.let {
            commandConsumer(command)
            return command
        }

        return null

    }

    fun getCommand(name: String, ownerOnly: Boolean = false): Command? = getCommand(name, ownerOnly) {}

    fun getChild(command: Command, args: List<String>): Pair<Int, Command> {

        return getChild(command, args, 0)

    }

    fun getChild(command: Command, args: List<String>, index: Int): Pair<Int, Command> {

        if (args.isEmpty()) return Pair(index, command)

        if (command.child.isNotEmpty() && command.child.map(Command::name).contains(args[0].toLowerCase())) {
            return getChild(command.child.first { it.name == args[0].toLowerCase() }, args.drop(1), index + 1)
        }

        return Pair(index, command)

    }

    fun registerCommands(vararg commands: Command) {

        commands.forEach {
            sarano.logger.info { "${it.name} registered" }
        }

        this.commands.addAll(commands)
    }

    private fun registerSlash() {

        for (command in commands.filter { it.canSlash }) {

            val slashCommand = CommandData(command.name, command.description)

            for (argument in command.arguments.sortedBy { it.optional }) {
                slashCommand.addOption(
                    OptionData(argument.type, argument.name, argument.description)
                        .setRequired(!argument.optional).apply {
                            for (choice in argument.choices) {

                                when (choice.value) {
                                    is Long -> {
                                        addChoice(choice.key, (choice.value as Long).toInt())
                                    }
                                    is String -> {
                                        addChoice(choice.key, choice.value as String)
                                    }
                                    else -> {
                                        sarano.logger
                                            .error { "Invalid choice type for command ${command.name} (${argument.name})" }
                                    }
                                }

                            }
                        }
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
