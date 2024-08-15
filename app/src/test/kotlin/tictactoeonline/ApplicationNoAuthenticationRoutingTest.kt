package tictactoeonline

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationNoAuthenticationRoutingTest {

    @Test
    fun `test root path`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Welcome to Tic-Tac-Toe Online", response.content)
            }
        }
    }


    @Test
    fun `no authentication required GET help`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/help").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val help = Json.decodeFromString<HelpPayload>(response.content!!)
                assertEquals(help(), help)
                assertEquals(HttpProtocolVersion.HTTP_1_1.toString(), request.version)
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    @Test
    fun `no authentication required GET helloWorld`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/helloWorld") {
                addHeader("Monkey", "from the jungle")
                addHeader("Pig", "from the farm")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, World!", response.content)
                println("Headers : ${request.headers.entries().size}")
                request.headers.forEach { s, slist ->
                    println("$s:  $slist")
                }
                println(
                    """
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                    request.contentType():     ${request.contentType()}    
                    request.cacheControl():    ${request.cacheControl()}    
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                """.trimIndent()
                )

            }
        }

    }

    @Test
    fun `no authentication required POST helloWorld`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/helloWorld") {
                addHeader("Monkey", "from the jungle")
                addHeader("Pig", "from the farm")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("POST: Hello, World!", response.content)
                println("Headers : ${request.headers.entries().size}")
                request.headers.forEach { s, slist ->
                    println("$s:  $slist")
                }
                println(
                    """
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                    request.contentType():     ${request.contentType()}    
                    request.cacheControl():    ${request.cacheControl()}    
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                """.trimIndent()
                )

            }
        }

    }

    @Test
    fun `work with info`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/info").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val infoPayload = Json.decodeFromString<InfoPayload>(response.content!!)
                println(
                    """
                    response.content:  ${response.content}
                    infoPayload: $infoPayload
                """.trimIndent()
                )
                assertEquals("/info", infoPayload.uri)
                assertEquals(HttpMethod.Get.toString(), infoPayload.http_method)
                assertEquals(80, infoPayload.port)
                assertEquals(0, infoPayload.num_users)
                assertEquals(0, infoPayload.num_users_signin)
                assertEquals(0, infoPayload.num_parameters)
            }
        }

    }


}