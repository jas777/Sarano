package com.sarano.command

import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

abstract class Command(protected val sarano: Sarano) {

    abstract val name: String
    abstract val description: String

    open val aliases: Array<String> = emptyArray()

    open val child: Array<Command> = emptyArray()

    open val ownerOnly: Boolean = false

    open val cooldown: Int = 3

    abstract fun execute(sender: Member, channel: TextChannel, message: Message, guild: Guild, args: List<String>)

}