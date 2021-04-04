package com.sarano.modules.core

import com.sarano.main.Sarano
import com.sarano.module.Module
import com.sarano.modules.core.commands.HelpCommand

class CoreModule(sarano: Sarano) : Module(sarano) {

    override val name: String = "core"
    override val description: String = "The core module contains all essential commands"

    override fun setup() {
        sarano.commandHandler.registerCommands(HelpCommand(sarano, this))
    }

}