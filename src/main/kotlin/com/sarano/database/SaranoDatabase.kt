package com.sarano.database

import com.sarano.config.Configuration
import com.sarano.main.Sarano
import org.jetbrains.exposed.sql.Database

class SaranoDatabase constructor(configuration: Configuration) {

    val database: Database = Database.connect(
        "jdbc:pgsql://${configuration.url}",
        "com.impossibl.postgres.jdbc.PGDriver",
        configuration.user,
        configuration.password
    )

}