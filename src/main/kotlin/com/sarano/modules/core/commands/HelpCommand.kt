package com.sarano.modules.core.commands

import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.sarano.command.Command
import com.sarano.command.CommandContext
import com.sarano.command.CommandHandler
import com.sarano.command.argument.CommandArgument
import com.sarano.module.Module
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType

class HelpCommand(private val commandHandler: CommandHandler) : Command {

    override val name: String = "help"
    override val description: String = "Provides explanation on how to use commands and/or modules"

    override val canSlash: Boolean = true

    override val cooldown: Int = 5

    // Debugging was fun :)
    // override val userPermissions: Array<Permission> = arrayOf(Permission.ADMINISTRATOR)

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument(
            "command_or_module", "The module or command you want to get more information about",
            OptionType.STRING, true
        )
    )

    override fun execute(ctx: CommandContext) {

        if (ctx.slash) ctx.slashEvent!!.deferReply(true).queue()

        ctx.debug(ctx.args.parsedArguments.map { "${it.key} - ${it.value.result}" }.joinToString(" "))

        var builder = ctx.sarano.defaultEmbed()

        ctx.args.parsedArguments["command_or_module"] ?: run {
            defaultHelp(ctx, builder)
            ctx.reply(builder.build())
            return
        }

        val input: String = ctx.args.string("command_or_module")

        ctx.debug("Input: $input")

        val command = commandHandler.getCommand(input)

        builder = when {

            command != null -> {
                commandHelp(command, builder)
            }

            ctx.sarano.modules.map { it.name }.contains(input) -> {
                moduleHelp(ctx.sarano.modules.find { it.name == input }!!, builder)
            }

            else -> {
                defaultHelp(ctx, builder)
            }

        }

        ctx.reply(builder.build())

    }

    private fun commandHelp(command: Command, builder: EmbedBuilder): EmbedBuilder {

        val joint = command.arguments.joinToString("\n")
        { "`${if (it.optional) "[${it.name}]" else "<${it.name}>"}` - ${it.description}" }

        builder
            .setTitle("Command help - ${command.name}")
            .setDescription("""
                ${command.description}
                
                **Aliases:** ${
                if (command.aliases.isEmpty()) "None..." else "`${command.aliases.joinToString("`, `")}`"
            }
                
                **Usage:**
                `${command.name} ${
                command.arguments.joinToString(" ")
                { if (it.optional) "[${it.name}]" else "<${it.name}>" }
            }`
                
                **Arguments**
                
                ${joint.ifEmpty { "_None..._" }}
                    
                `[name]` - Optional argument
                `<name>` - Required argument
                
            """.trimIndent())

        return builder

    }

    private fun moduleHelp(module: Module, builder: EmbedBuilder): EmbedBuilder {

        val moduleCommands = module.commands

        builder
            .setTitle("Module help - ${module.name}")
            .setDescription(
                """
                ${module.description}
                
                **Commands**
                ${
                    if (moduleCommands.isNotEmpty()) "`${moduleCommands.joinToString("`, `") { it.name }}`"
                    else "None..."
                }
                
                """.trimIndent()
            )

        return builder
    }

    private fun defaultHelp(ctx: CommandContext, builder: EmbedBuilder): EmbedBuilder {

        builder
            .setTitle("${ctx.sarano.client.shards.first().selfUser.name} - help")
            .setDescription("Hi! My name is **${ctx.channel.jda.selfUser.name}**! To get more information about a module" +
                    "or a command, use `${ctx.sarano.configuration.prefix}help [command _or_ module] :D`")

        return builder
    }

}
