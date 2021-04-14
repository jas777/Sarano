package com.sarano.modules.core.commands

import com.sarano.command.Command
import com.sarano.command.CommandContext
import java.util.*

class PingCommand : Command {

    override val name: String = "ping"
    override val description: String = "Pong!"

    override val canSlash: Boolean = false

    override fun execute(ctx: CommandContext) {

        ctx.channel.sendMessage("Pong! `Xms`").queue {
            it.editMessage("Pong! `${it.timeCreated.toInstant().toEpochMilli() - Date().time}ms`").queue()
        }

    }

}
