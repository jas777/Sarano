package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.Permission
import com.sarano.module.Module
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

abstract class Command(val sarano: Sarano, val module: Module? = null) {

    abstract val name: String
    abstract val description: String

    open val aliases: Array<String> = emptyArray()

    open val userPermissions: Array<Permission> = emptyArray()
    open val botPermissions: Array<Permission>  = arrayOf(Permission.MESSAGE_WRITE)

    open val arguments: Array<CommandArgument> = emptyArray()

    open val child: Array<Command> = emptyArray()

    open val ownerOnly: Boolean = false

    open val cooldown: Int = 3

    open val canSlash: Boolean = false

    abstract fun execute(ctx: CommandContext)

    // abstract fun executeSlash(event: SlashCommandEvent)

}