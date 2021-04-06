package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Command.OptionType

class TestCommand(sarano: Sarano) : Command(sarano) {

    override val name: String = "test"

    override val description: String = "test command"

    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument("number", "testing parser 1", OptionType.INTEGER, false,
            choices = hashMapOf(Pair("One", 1), Pair("Two", 2), Pair("Triple seven", 777))
        ),
        CommandArgument("optional", "testing parser 2", OptionType.INTEGER, true),
        CommandArgument("text", "testing parser 3", OptionType.STRING, false, 2,
            choices = hashMapOf(Pair("1", "One"), Pair("2", "Two"), Pair("777", "Triple seven"))
        )
    )

    override val aliases: Array<String> = arrayOf("test1", "test2")

    override fun execute(ctx: CommandContext) {

        ctx.reply("Number: ${ctx.args["number"]?.result as Long}\nText: " +
                (ctx.args["text"]?.result as List<*>).joinToString(" ") +
                "\nOptional int: ${ctx.args["optional"]?.result as Long?}")

    }

//    override fun executeSlash(event: SlashCommandEvent) {
//        event.reply().queue()
//    }

}
