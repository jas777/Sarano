package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.util.input
import net.dv8tion.jda.api.interactions.commands.OptionType

class TestCommand : Command {

    override val name: String = "test"

    override val description: String = "test command"

    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument(
            "number", "testing parser 1", OptionType.INTEGER, false,
            choices = hashMapOf(Pair("One", 1L), Pair("Two", 2L), Pair("Triple seven", 777L))
        ),
        CommandArgument(
            "text", "testing parser 3", OptionType.STRING, false, 2,
            choices = hashMapOf(Pair("1", "One"), Pair("2", "Two"), Pair("777", "Triple seven"))
        ),
        CommandArgument("optional", "testing parser 2", OptionType.INTEGER, true)
    )

    override val aliases: Array<String> = arrayOf("test1", "test2")

    override fun execute(ctx: CommandContext) {

        ctx.channel.input("Please provide test input :)", ctx.sender.user) {
            it.channel.sendMessage(it.message.contentDisplay).queue()
        }

        ctx.reply(
            "Number: ${ctx.args.long("number")}\nText: " +
                    (ctx.args.stringList("text")).joinToString(" ") +
                    "\nOptional int: ${ctx.args.optional<Long>("optional") ?: "Null"}"
        )

    }

}
