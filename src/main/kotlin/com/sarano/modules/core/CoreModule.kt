package com.sarano.modules.core

import com.sarano.command.Command
import com.sarano.main.Sarano
import com.sarano.module.Module
import com.sarano.modules.core.commands.HelpCommand

class CoreModule(val sarano: Sarano) : Module {

    override val name: String = "core"
    override val description: String = "The core module contains all essential commands"

    override val commands: Array<Command> = arrayOf(HelpCommand(sarano.commandHandler))

    override fun setup() {}

}