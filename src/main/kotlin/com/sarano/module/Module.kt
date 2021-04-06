package com.sarano.module

import com.sarano.command.Command
import com.sarano.main.Sarano

abstract class Module constructor(val sarano: Sarano) {

    abstract val name: String
    abstract val description: String

    open val commands: Array<Command> = emptyArray()

    abstract fun setup(): Unit

}
