package com.sarano.command

import com.sarano.command.argument.CommandArgument
import com.sarano.main.Sarano
import net.dv8tion.jda.api.Permission
import com.sarano.module.Module
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface Command {

    val name: String
    val description: String

    val aliases: Array<String> get() = emptyArray()
    val userPermissions: Array<Permission> get() = emptyArray()
    val botPermissions: Array<Permission> get() = arrayOf(Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS)
    val arguments: Array<CommandArgument> get() = emptyArray()
    val child: Array<Command> get() = emptyArray()

    val ownerOnly: Boolean get() = false
    @Deprecated("All commands are slash by default", ReplaceWith("true"))
    val canSlash: Boolean get() = true
    val ephemeral: Boolean get() = true

    val cooldown: Int get() = 3

    fun execute(ctx: CommandContext)

    fun handleButtonClick(
        event: ButtonInteractionEvent, buttonId: String, sender: User, originalUser: String,
        arguments: Array<String>
    ) {
        error("This command cannot handle button clicks")
    }

}
