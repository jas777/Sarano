package com.sarano.command

import com.sarano.main.Sarano
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

abstract class Command(protected val sarano: Sarano) {

    abstract val name: String
    abstract val description: String

    abstract val ownerOnly: Boolean

    abstract val cooldown: Int

    abstract fun execute(sender: Member, channel: TextChannel, message: Message, guild: Guild)

}