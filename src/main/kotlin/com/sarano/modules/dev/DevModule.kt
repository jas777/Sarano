package com.sarano.modules.dev

import com.sarano.command.Command
import com.sarano.main.Sarano
import com.sarano.module.Module
import com.sarano.modules.dev.commands.EvalCommand

class DevModule(sarano: Sarano) : Module() {

    override val name: String = "dev"
    override val description: String = "dev"

    override val commands: Array<Command> = arrayOf(
        EvalCommand()
    )

    override fun setup() {}

}