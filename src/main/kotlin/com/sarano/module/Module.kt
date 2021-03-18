package com.sarano.module

abstract class Module constructor(val name: String, val description: String, val boolean: Boolean) {

    abstract fun setup(): Unit

}
