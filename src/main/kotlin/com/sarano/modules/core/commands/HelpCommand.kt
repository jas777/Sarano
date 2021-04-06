package com.sarano.modules.core.commands

import com.sarano.command.Command
import com.sarano.command.CommandContext
import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import com.sarano.module.Module
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Command.OptionType

class HelpCommand(sarano: Sarano, module: Module) : Command(sarano, module) {

    override val name: String = "help"
    override val description: String = "Provides explanation on how to use commands and/or modules"

    override val canSlash: Boolean = true

    override val cooldown: Int = 5

    // Debugging was fun :)
    // override val userPermissions: Array<Permission> = arrayOf(Permission.ADMINISTRATOR)

    // override val botPermissions: Array<Permission> = arrayOf(Permission.ADMINISTRATOR, Permission.BAN_MEMBERS)

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument(
            "command_or_module", "The module or command you want to get more information about",
            OptionType.STRING, true, choices = hashMapOf(*mutableListOf<Pair<String, Any>>(Pair("help", "help"))
                .apply {

                    addAll((sarano.commandHandler.commands.filter { !it.ownerOnly }.map {
                        Pair(it.name, it.name)
                    }))

                    addAll(
                        sarano.modules.map { Pair("(Module) ${it.name}", it.name) }
                    )

                    sarano.logger.debug { joinToString(" ") { it.first } }

                }.toTypedArray()
            )
        )
    )

    override fun execute(ctx: CommandContext) {

        ctx.debug(ctx.args.map { "${it.key} - ${it.value.result}" }.joinToString(" "))

        var builder = sarano.defaultEmbed()

        if (ctx.args["command_or_module"] == null) {
            defaultHelp(builder)
            ctx.reply(builder.build())
            return
        }

        val input: String = ctx.args["command_or_module"]!!.result as String

        ctx.debug("Input: $input")

        val command = sarano.commandHandler.getCommand(input)

        builder = when {

            command != null -> {
                commandHelp(command, builder)
            }

            sarano.modules.map { it.name }.contains(input) -> {
                moduleHelp(sarano.modules.find { it.name == input }!!, builder)
            }

            else -> {
                defaultHelp(builder)
            }

        }

        ctx.reply(builder.build())

    }

    private fun commandHelp(command: Command, builder: EmbedBuilder): EmbedBuilder {

        builder
            .setTitle("Command help - ${command.name}")
            .setDescription("""
                ${command.description}
                
                **Aliases:** ${
                if (command.aliases.isEmpty()) "None..." else "`${command.aliases.joinToString("`, `")}`"
            }
                
                **Arguments**
                
                ${
                command.arguments.joinToString("\n")
                { "`${if (it.optional) "[${it.name}]" else "<${it.name}>"}` - ${it.description}" }
            }
                    
                `[name]` - Optional argument
                `<name>` - Required argument
                
            """.trimIndent())

        return builder

    }

    private fun moduleHelp(module: Module, builder: EmbedBuilder): EmbedBuilder {

        val moduleCommands = sarano.commandHandler.commands.filter { it.module?.name == module.name }

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

    private fun defaultHelp(builder: EmbedBuilder): EmbedBuilder {

        builder
            .setTitle("${sarano.client.shards.first().selfUser.name} - help")
            .setDescription("")

        return builder
    }

}