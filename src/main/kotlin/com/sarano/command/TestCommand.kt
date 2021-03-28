package com.sarano.command

import com.sarano.command.argument.Arguments
import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.Command.OptionType
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class TestCommand(sarano: Sarano) : Command(sarano) {

    override val name: String = "test"

    override val description: String = "test command"

    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument("number", "testing parser 1", OptionType.INTEGER, false),
        CommandArgument("optional", "testing parser 2", OptionType.INTEGER, true),
        CommandArgument("text", "testing parser 3", OptionType.STRING, false, 2)
    )

    override fun execute(sender: Member, channel: TextChannel, message: Message, guild: Guild, args: List<String>) {

        val arguments = Arguments(this, args)

        channel.sendMessage("" + arguments.parsedArguments["number"]?.result?.get() as Int).queue()

        if (arguments.parsedArguments.containsKey("optional")) {
            channel.sendMessage("" + arguments.parsedArguments["optional"]?.result?.get() as Int).queue()
        }

        channel.sendMessage("" + (arguments.parsedArguments["text"]?.result?.get() as List<*>)
            .joinToString(" ")).queue()

    }

    override fun executeSlash(event: SlashCommandEvent) {
        event.reply("Number: ${event.getOption("number")?.asLong}\nText: ${event.getOption("text")?.asString}" +
                "\nOptional int: ${event.getOption("optional")?.asLong}").queue()
    }

}
