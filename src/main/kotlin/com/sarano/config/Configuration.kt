package com.sarano.config

class Configuration {

    // General bot settings

    val token: String = ""
    val prefix: String = ""

    val developers: List<String> = ArrayList()

    // Default embed settings

    val embedColor: String = ""

    val errorColor: String = ""
    val warnColor: String = ""
    val successColor: String = ""

    val embedFooter: String = ""

    // Database settings

    val ip: String = ""
    val port: Long? = 0L
    val database: String = ""
    val user: String = ""
    val password: String = ""

}
