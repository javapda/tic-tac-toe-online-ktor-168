package tictactoeonline.util

import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tictactoeonline.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals

class GamesTest {
    @BeforeEach
    fun setup() {
        clearAll()
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun `test games need at least 2 games`() {
        APPLICATION_TESTING = true
        // start a game
        fun doGame(emailUser1: String, emailUser2: String, fieldSize: String = "3x3", gameId: Int) {
            withTestApplication(Application::module) {
                // signup players
                lateinit var user1: User
                lateinit var user2: User
                // add a person
                var email1 = emailUser1
                var password1 = "1111"
                var email2 = emailUser2
                var password2 = "2222"
                val example1Size = fieldSize

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    user1 = User(email = email1, password = password1)
                    val json = Json.encodeToString(user1)
                    setBody(json)

                }.apply {
                    assertEquals(1 + ((gameId - 1) * 2), UserStore.size)
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    user2 = User(email = email2, password = password1)
                    val json = Json.encodeToString(user2)
                    setBody(json)

                }.apply {

                }

                // sign in players
                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(Json.encodeToString(user1))

                }.apply {

                }
                user1 = UserSignedInStore.find { user -> user == user1 }!!
                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(Json.encodeToString(user2))
                }
                user2 = UserSignedInStore.find { user -> user == user2 }!!

                // start a game
                handleRequest(HttpMethod.Post, "/game") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    val payloadOfJwt: String = JWT().decodeJwt(user1.jwt).payload
                    val contents = String(Base64.decode(payloadOfJwt))

                    @Serializable
                    data class Payload(val email: String)

                    val payload = Json.decodeFromString<Payload>(contents)
                    val email = payload.email
                    addHeader("Authorization", "Bearer ${user1.jwt}")

                    val ngr: NewGameRequestPayload =
                        NewGameRequestPayload(
                            player1 = user1.email,
                            player2 = "",
                            size = example1Size,
                            privateRoom = false
                        )
                    val json = Json.encodeToString(ngr)
                    setBody(json)

                }.apply {
                    assertEquals(gameId, GameStore.size)
                }

                // join the game
                handleRequest(HttpMethod.Post, "/game/$gameId/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(HttpHeaders.Authorization, "Bearer ${user2.jwt}")

                }


            }
        }

        fun gamesCheck(user: User, expectedGameCount: Int) {
            withTestApplication(Application::module) {
                // games call
                handleRequest(HttpMethod.Get, "/games") {
                    addHeader(HttpHeaders.Authorization, "Bearer ${user.jwt}")
                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    val payload = Json.decodeFromString<List<GamesResponsePayload>>(response.content.toString())
                    assertEquals(expectedGameCount, payload.size)
                }

            }
        }

        fun gamesCheckWithoutAuthorization(user: User) {
            withTestApplication(Application::module) {
                // games call - without authorization
                handleRequest(HttpMethod.Get, "/games").apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                }

            }
        }
        doGame("carl@example.com", "mike@example.com", "4x3", 1)
        gamesCheck(UserSignedInStore.first(), 1)
        gamesCheckWithoutAuthorization(UserSignedInStore.first())
        doGame("artem@example.com", "artem@example.com", "3x3", 2)
        gamesCheck(UserSignedInStore.first(), 2)

    }


}