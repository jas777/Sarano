package com.sarano.modules.dev.commands

import com.sarano.command.Command
import com.sarano.command.CommandContext
import com.sarano.command.argument.CommandArgument
import net.dv8tion.jda.api.interactions.commands.OptionType
import javax.script.*

class EvalCommand : Command {

    override val name: String = "eval"
    override val description: String = "Used to eval code"

    override val ownerOnly: Boolean = true
    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument("code", "Code to evaluate", OptionType.STRING, false, -1)
    )

    override fun execute(ctx: CommandContext) {

        if (ctx.slash) ctx.slashEvent!!.deferReply().queue()

        val code = (ctx.args.stringList("code"))
            .joinToString(" ")
            .replace("kt", "")
            .replace("`", "")

        val engine = ScriptEngineManager().apply {
            this.bindings = SimpleBindings().apply {
                set("ctx", ctx)
            }
        }.getEngineByExtension("kts")

        var error: String? = null

        val result = try {
            engine.eval(code)
        } catch (exception: ScriptException) {
            error = exception.message
        }

        var builder = ctx.sarano.successEmbed()

        if (error == null) {
            builder.setTitle("Eval result").setDescription(
                "```${
                    result?.toString()
                        ?.replace(ctx.channel.jda.token, "<TOKEN>") ?: "void"
                }```"
            )
        } else {
            builder = ctx.sarano.errorEmbed()
            builder.setTitle("Eval result").setDescription("```${error}```")
        }

        ctx.reply(builder.build())

    }

}
