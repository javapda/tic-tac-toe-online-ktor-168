package tictactoeonline

import io.ktor.application.*
import io.ktor.server.netty.*

class MyApplication {
    fun start(args: Array<String>) {
        EngineMain.main(args)
    }
}

fun main(args: Array<String>) {
    MyApplication().start(args)
}

/**
 * Application Testing
 * Used to prevent maintaining data between runs of the test infrastructure
 */
var APPLICATION_TESTING = false
fun Application.module(testing: Boolean = false) {
    if (!APPLICATION_TESTING) clearAll()
    fun showQueryParameters(call: ApplicationCall) {
        call.application.environment.log.info("No. parameters: ${call.request.queryParameters.entries().size}")
        call.request.queryParameters.forEach { key, value ->
            call.application.environment.log.info("$key : $value")
        }
    }

    configureDatabase()
    configureContentNegotiationForJsonSerializationAndDeserialization()
    configureAuthenticationAndAuthorization()
    configureRouting()

}

fun Application.anotherModule(testing: Boolean = false) {
//    println("Hello from anotherModule, testing=$testing")
}