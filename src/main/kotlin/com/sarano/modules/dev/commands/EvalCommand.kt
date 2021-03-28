package com.sarano.modules.dev.commands

import com.sarano.command.Command
import com.sarano.command.argument.Arguments
import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import com.sarano.module.Module
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.entities.Command.OptionType
import javax.script.*

class EvalCommand(sarano: Sarano, module: Module) : Command(sarano, module) {

    override val name: String = "eval"
    override val description: String = "Used to eval code"

    override val ownerOnly: Boolean = true
    override val canSlash: Boolean = true

    override val arguments: Array<CommandArgument> = arrayOf(
        CommandArgument("code", "Code to evaluate", OptionType.STRING, false, -1)
    )

    override fun execute(sender: Member, channel: TextChannel, message: Message, guild: Guild, args: List<String>) {

        val arguments = Arguments(this, args)

        eval((arguments.parsedArguments["code"]?.result?.get() as List<*>)
            .joinToString(" ")
            .replace("kt", "")
            .replace("`", ""), channel, sender)

    }

    override fun executeSlash(event: SlashCommandEvent) {

        event.getOption("code")?.asString
            ?.replace("kt", "")?.replace("`", "")?.let {
                eval(it, event.textChannel, event.member, slash = true, event)
            }

    }

    private fun eval(code: String, channel: TextChannel, sender: Member?, slash: Boolean = false, slashCommandEvent: SlashCommandEvent? = null) {

        val engine = ScriptEngineManager().apply {
            this.bindings = SimpleBindings().apply {
                set("sarano", sarano)
                set("channel", channel)
                set("sender", sender)
            }
        }.getEngineByExtension("kts")

        var error: String? = null

        val result = try {
            engine.eval(code)
        } catch (exception: ScriptException) {
            error = exception.message
        }

        val builder = sarano.defaultEmbed()

        if (error == null) {
            builder.setTitle("Eval result").setDescription("```${result?.toString()
                ?.replace(sarano.client.shards.first().token, "<TOKEN>") ?: "void"}```")
        } else {
            builder.setTitle("Eval result").setDescription("```${error}```")
        }

        if (!slash) {
            channel.sendMessage(builder.build()).queue()
        } else {

            assert(slashCommandEvent != null)

            slashCommandEvent?.reply(builder.build())?.queue()

        }

    }

}