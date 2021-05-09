package com.sarano.database

import com.sarano.config.Configuration
import com.sarano.main.Sarano
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class SaranoDatabase constructor(configuration: Configuration) {

    val connection: Database

    init {

        val config = HikariConfig()

        config.jdbcUrl = "jdbc:pgsql://${configuration.url}"
        config.username = configuration.user
        config.password = configuration.password
        config.dataSourceClassName = "com.impossibl.postgres.jdbc.PGDataSource"

        val hikariDataSource = HikariDataSource(config)

        connection = Database.connect(hikariDataSource)

    }

}
