package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.util.input
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.components.Button

class TestCommand : Command {

    override val name: String = "test"

    override val description: String = "test command"

    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
//        CommandArgument(
//            "number", "testing parser 1", OptionType.INTEGER, false,
//            choices = hashMapOf(Pair("One", 1L), Pair("Two", 2L), Pair("Triple seven", 777L))
//        ),
//        CommandArgument(
//            "text", "testing parser 3", OptionType.STRING, false, 2,
//            choices = hashMapOf(Pair("1", "One"), Pair("2", "Two"), Pair("777", "Triple seven"))
//        ),
//        CommandArgument("optional", "testing parser 2", OptionType.INTEGER, true)
    )

    override val aliases: Array<String> = arrayOf("test1", "test2")

    override fun execute(ctx: CommandContext) {

//        ctx.channel.input("Please provide test input :)", ctx.sender.user) {
//            it.channel.sendMessage(it.message.contentDisplay).queue()
//        }

        if (!ctx.slash) {
//            ctx.reply(
//                "Number: ${ctx.args.long("number")}\nText: " +
//                        (ctx.args.stringList("text")).joinToString(" ") +
//                        "\nOptional int: ${ctx.args.optional<Long>("optional") ?: "Null"}"
//            )
        } else {
            ctx.slashReply()
                .addEmbeds(ctx.sarano.successEmbed().setDescription("I hope this works").build())
                .addActionRow(
                    Button.primary(ctx.generateButtonId(name, "prm"), "Primary"),
                    Button.secondary(ctx.generateButtonId(name, "scd"), "Secondary")
                ).addActionRow(
                    Button.success(ctx.generateButtonId(name, "scs"), "It does!"),
                    Button.danger(ctx.generateButtonId(name, "dgr"), "It doesn't"),
                    Button.link("https://en.wikipedia.org/wiki/Schr%C3%B6dinger%27s_cat", "Or does it?")
                ).queue()
        }

    }

    override fun handleButtonClick(
        event: ButtonClickEvent,
        buttonId: String,
        sender: User,
        originalUser: String,
        arguments: Array<String>
    ) {
        if (sender.id != originalUser) return;
        when (buttonId) {
            "prm" -> event.reply("Primary clicked!").queue()
            "scd" -> event.reply("Secondary clicked!").queue()
            "scs" -> event.reply("Aye!").queue()
            "dgr" -> event.reply("Aw :(!").queue()
        }
    }

}
