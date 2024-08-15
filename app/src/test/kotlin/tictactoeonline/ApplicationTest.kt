package tictactoeonline

import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

fun emailFromJwt(jwt: String): String =
    JWT.require(algorithm).build().verify(jwt).getClaim("email").asString()
class ApplicationTest {

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

}