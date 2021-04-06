package com.sarano.database

import com.sarano.main.Sarano
import org.jetbrains.exposed.sql.Database

class SaranoDatabase constructor(sarano: Sarano) {

    val database: Database = Database.connect(
        "jdbc:pgsql://${sarano.configuration.url}",
        "com.impossibl.postgres.jdbc.PGDriver",
        sarano.configuration.user,
        sarano.configuration.password
    )

}