package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.Permission
import com.sarano.module.Module
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

interface Command {

    val name: String
    val description: String

    val aliases: Array<String> get() = emptyArray()

    val userPermissions: Array<Permission> get() = emptyArray()
    val botPermissions: Array<Permission> get() = arrayOf(Permission.MESSAGE_WRITE)

    val arguments: Array<CommandArgument> get() = emptyArray()

    val child: Array<Command> get() = emptyArray()

    val ownerOnly: Boolean get() = false

    val cooldown: Int get() = 3

    val canSlash: Boolean get() = false

    fun execute(ctx: CommandContext)

}
