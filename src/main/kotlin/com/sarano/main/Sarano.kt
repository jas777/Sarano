package com.sarano.main

import com.sarano.command.CommandHandler
import com.sarano.command.TestCommand
import com.sarano.config.Configuration
import com.sarano.module.Module
import com.sarano.modules.core.commands.HelpCommand
import com.sarano.modules.core.commands.PingCommand
import com.sarano.modules.dev.commands.EvalCommand
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
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        println("Please supply the config path!")
        exitProcess(1)
    } else {

        Sarano(args[0], args.size > 1).apply {

            // Module registration

            modules.addAll(
                listOf(
                    Module("dev", "Dev module", arrayOf(EvalCommand()), emptyArray()),
                    Module(
                        "core", "Contains all essential commands",
                        arrayOf(
                            HelpCommand(commandHandler),
                            PingCommand()
                        ),
                        arrayOf(commandHandler)
                    )
                )
            )

            // Command registration (!DEV ONLY, USE MODULES!)

            commandHandler.registerCommands(TestCommand())

        }.start()

    }

}

class Sarano constructor(config: String, val debug: Boolean) {

    var configuration: Configuration

    val logger: KLogger = LoggerFactory.getLogger("main").toKLogger()

    lateinit var client: ShardManager

    val commandHandler: CommandHandler = CommandHandler(this)

    val modules: MutableList<Module> = ArrayList()

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

    }

    fun start() {

        // Shard manager setup

        client = DefaultShardManagerBuilder
            .createDefault(configuration.token)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .build()

        // Module init

        modules.forEach {
            commandHandler.registerCommands(*it.commands)
            client.addEventListener(*it.listeners)
        }

    }

    fun defaultEmbed(): EmbedBuilder {
        return EmbedBuilder().setFooter(configuration.embedFooter).setColor(parseColor(configuration.embedColor))
    }

    fun errorEmbed(): EmbedBuilder {
        return EmbedBuilder().setFooter(configuration.embedFooter).setColor(parseColor(configuration.errorColor))
    }

    fun warnEmbed(): EmbedBuilder {
        return EmbedBuilder().setFooter(configuration.embedFooter).setColor(parseColor(configuration.warnColor))
    }

    fun successEmbed(): EmbedBuilder {
        return EmbedBuilder().setFooter(configuration.embedFooter).setColor(parseColor(configuration.successColor))
    }

    private fun parseColor(value: String): Color {
        return try {
            Color.decode("#${value}")
        } catch (exception: NumberFormatException) {
            logger.error { "Invalid embed color!" }
            Color.WHITE
        }
    }

    operator fun plus(sarano: Sarano): Sarano {
        logger.info { "AAaaa" }
        return sarano
    }

}
