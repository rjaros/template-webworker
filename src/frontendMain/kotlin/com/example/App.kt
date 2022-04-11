package com.example

import io.kvision.Application
import io.kvision.BootstrapCssModule
import io.kvision.BootstrapModule
import io.kvision.CoreModule
import io.kvision.html.div
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
import org.w3c.dom.MessageEvent
import org.w3c.dom.Worker

class App : Application() {

    override fun start(state: Map<String, Any>) {
        val worker = Worker("worker.js")
        worker.onmessage = { m: MessageEvent ->
            println("Client got message: ${m.data}")
        }
        println("Client sending message: test")
        worker.postMessage("test")
        root("kvapp") {
            div("test")
        }
    }
}

fun main() {
    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, CoreModule)
}
