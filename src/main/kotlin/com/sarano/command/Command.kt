package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import com.sarano.module.Module
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

abstract class Command(val sarano: Sarano, val module: Module? = null) {

    abstract val name: String
    abstract val description: String

    open val aliases: Array<String> = emptyArray()

    open val arguments: Array<CommandArgument> = emptyArray()

    open val child: Array<Command> = emptyArray()

    open val ownerOnly: Boolean = false

    open val cooldown: Int = 3

    open val canSlash: Boolean = false

    abstract fun execute(sender: Member, channel: TextChannel, message: Message, guild: Guild, args: List<String>)

    abstract fun executeSlash(event: SlashCommandEvent)

}