package com.sarano.util

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.TimeUnit

fun TextChannel.input(text: String, user: User, action: (GuildMessageReceivedEvent) -> Unit) {

    sendMessage(text).queue()

    getWaiter(jda)!!.waitForGuildMessageReceived(
        {
            event -> event.author.id == user.id &&
                event.channel.id == id
        },
        action,
        30,
        TimeUnit.SECONDS,
        {
            sendMessage("Input timed out!").queue()
        }
    )

}