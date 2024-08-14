package tictactoeonline

import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tictactoeonline.util.*
import kotlin.test.assertEquals

class ApplicationTest {
    fun emailFromJwt(jwt: String): String =
        JWT.require(algorithm).build().verify(jwt).getClaim("email").asString()

    @BeforeEach
    fun setup() {
        clearAll()
    }

    @Test
    fun `game status failed authorization`() {
        withTestApplication(Application::module) {
            val game_id = 1
            handleRequest(HttpMethod.Get, "/game/$game_id/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("Monkey", "Gorilla")
            }.apply {
                println(
                    """
                    game status failed authorization
                    response.status():   ${response.status()}
                    response.content:    ${response.content}
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

    @Test
    fun testRoot() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, World!", response.content)
            }
        }
    }

    @Test
    fun `no authentication GET helloWorld`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/helloWorld"){
                addHeader("Monkey", "from the jungle")
                addHeader("Pig", "from the farm")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, World!", response.content)
                println("Headers : ${request.headers.entries().size}")
                request.headers.forEach{ s, slist ->
                    println("$s:  $slist")
                }
                println("""
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                    request.contentType():     ${request.contentType()}    
                    request.cacheControl():    ${request.cacheControl()}    
                    request.acceptEncoding():  ${request.acceptEncoding()}    
                """.trimIndent())

            }
        }
//        client.get("/helloWorld").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("Hello World!", bodyAsText())
//            assertEquals(HttpProtocolVersion.HTTP_1_1, version)
//            // println("requestTime: $requestTime")
//            // println("responseTime: $responseTime")

    }


    @Test
    fun `get help`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/help").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                val expected = help()
//                assertEquals(11, expected.endpoints.size)
//                assertEquals(HttpProtocolVersion.HTTP_1_1, version)
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("Hello, World!", response.content)
            }
        }
    }

    @Test
    fun `test with test application configuration`() = withTestApplication() {

        println("""
            environment.application.developmentMode:  ${environment.application.developmentMode}
            environment == environment.application.environment: ${environment == environment.application.environment}
            environment.config:  ${environment.config}
            """.trimIndent())
    }

    @Test
    fun testjwt() {
        assertEquals(emailCarl, emailFromJwt(jwtCarl))
        assertEquals(emailMike, emailFromJwt(jwtMike))
        assertEquals(emailArtem, emailFromJwt(jwtArtem))
    }


}