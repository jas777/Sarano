package com.sarano.util

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull

/**
 * I've just converted this code to Kotlin
 * @author John Grosh (john.a.grosh@gmail.com)
 */
class MessageWaiter : ListenerAdapter() {

    private val set: MutableSet<WaitingEvent> = mutableSetOf()
    private val threadpool = Executors.newSingleThreadScheduledExecutor()

    override fun onGenericEvent(event: GenericEvent) {
        if (event is GuildMessageReceivedEvent) onGuildMessageReceived(event)
    }

    @Synchronized
    fun waitForGuildMessageReceived(
        condition: (GuildMessageReceivedEvent) -> Boolean,
        action: (GuildMessageReceivedEvent) -> Unit,
        timeout: Long, unit: TimeUnit?,
        timeoutAction: () -> Unit?
    ) {
        val we = WaitingEvent(condition, action)
        set.add(we)
        if (timeout > 0 && unit != null) {
            threadpool.schedule({ if (set.remove(we)) timeoutAction() }, timeout, unit)
        }
    }

    @Synchronized
    override fun onGuildMessageReceived(@Nonnull event: GuildMessageReceivedEvent) {
        set.removeAll(set.filter { i -> i.attempt(event) })
    }

}

private class WaitingEvent(
    val condition: (GuildMessageReceivedEvent) -> Boolean,
    val action: (GuildMessageReceivedEvent) -> Unit
) {

    fun attempt(event: GuildMessageReceivedEvent): Boolean {
        if (condition(event)) {
            action(event)
            return true
        }
        return false
    }

}

fun getWaiter(jda: JDA): MessageWaiter? {
    for (ev in jda.eventManager.registeredListeners) if (ev is MessageWaiter) return ev
    return null
}
