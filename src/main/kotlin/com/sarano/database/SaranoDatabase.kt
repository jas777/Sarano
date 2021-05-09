package com.sarano.database

import com.sarano.config.Configuration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import java.util.*

class SaranoDatabase constructor(configuration: Configuration) {

    val connection: Database

    init {

        val config = HikariConfig()

        config.dataSourceClassName = "com.impossibl.postgres.jdbc.PGDataSource"

        config.addDataSourceProperty("databaseName", configuration.database)
        config.addDataSourceProperty("serverName", configuration.ip)
        config.addDataSourceProperty("portNumber", configuration.port!!.toInt())

        config.username = configuration.user
        config.password = configuration.password

        val hikariDataSource = HikariDataSource(config)

        connection = Database.connect(hikariDataSource)

    }

}
