package com.sarano.database

import com.sarano.config.Configuration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import java.util.*

class SaranoDatabase constructor(configuration: Configuration) {

    val connection: Database

    init {

        val props = Properties()
        props.setProperty("dataSourceClassName", "com.impossibl.postgres.jdbc.PGDataSource")
        props.setProperty("dataSource.databaseName", configuration.database)
        props.setProperty("dataSource.serverName", configuration.ip)
        props.setProperty("dataSource.portNumber", configuration.port.toString())

        val config = HikariConfig(props)

        config.username = configuration.user
        config.password = configuration.password

        val hikariDataSource = HikariDataSource(config)

        connection = Database.connect(hikariDataSource)

    }

}
