package com.sarano.main

import com.sarano.command.CommandHandler
import com.sarano.config.Configuration
import me.grison.jtoml.impl.Toml
import mu.KLogger
import mu.toKLogger
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println("Please supply the config path!")
        exitProcess(1)
    } else {
        Sarano(args[0], args.size > 1)
    }

}

class Sarano constructor(config: String, val debug: Boolean) {

    var configuration: Configuration

    val logger: KLogger = LoggerFactory.getLogger("main").toKLogger()

    val client: ShardManager

    val commandHandler: CommandHandler = CommandHandler(this)

    init {

        // Fetching configuration

        val configFile = File(config)

        logger.debug { "Running in debug mode..." }
        logger.debug { "Started config initialization (Path $config)" }

        if (!configFile.exists()) {

            try {

                configFile.createNewFile()

                configFile.writeText(Toml.serialize("bot", Configuration()))

                logger.info { "Created an empty config file - please re-run after filling all fields" }
                exitProcess(0)

            } catch (exception: IOException) {

                logger.error { exception.message }
                exitProcess(1)

            }

        } else {

            configuration = Toml.parse(configFile).getAs("bot", Configuration::class.java)
            logger.info { "Configuration loaded successfully!" }

        }

        // Command registration (!DEV ONLY, USE MODULES!)

        // commandHandler.registerCommands(ACommand(this))

        // Shard manager setup

        client = DefaultShardManagerBuilder
            .createDefault(configuration.token)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .build()

        // Event listeners registration

        client.addEventListener(commandHandler)

    }

    fun defaultEmbed(): EmbedBuilder {

        val color: Color = try {
            Color.decode(configuration.embedColor)
        } catch (exception: NumberFormatException) {
            logger.error { "Invalid embed color!" }
            Color.WHITE
        }

        return EmbedBuilder().setFooter(configuration.embedFooter).setColor(color)

    }

}
