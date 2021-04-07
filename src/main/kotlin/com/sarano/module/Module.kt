package com.sarano.module

import com.sarano.command.Command
import com.sarano.main.Sarano
import net.dv8tion.jda.api.hooks.ListenerAdapter

data class Module(
    val name: String, val description: String, val commands: Array<Command>,
    val listeners: Array<ListenerAdapter>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Module

        if (name != other.name) return false
        if (description != other.description) return false
        if (!commands.contentEquals(other.commands)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + commands.contentHashCode()
        return result
    }
}
