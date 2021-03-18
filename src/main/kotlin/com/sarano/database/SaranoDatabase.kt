package com.sarano.database

import org.jetbrains.exposed.sql.Database

class SaranoDatabase {

    val database: Database = Database.connect("")

}