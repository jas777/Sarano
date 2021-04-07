package com.sarano.module

import com.sarano.command.Command
import com.sarano.main.Sarano

interface Module {

    val name: String
    val description: String

    val commands: Array<Command> get() = emptyArray()

    fun setup()

}
