package tictactoeonline

import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals

fun emailFromJwt(jwt: String): String =
    JWT.require(algorithm).build().verify(jwt).getClaim("email").asString()

class ApplicationTest {

    @BeforeEach
    fun setup() {
        clearAll()
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun `Example 1 signup and signin two people`() {
        withTestApplication(Application::module) {
            lateinit var user1: User
            lateinit var user2: User
//            lateinit var response: HttpResponse
            // add a person
            var email1 = "carl@example.com"
            var password1 = "1111"
            var email2 = "mike@example.com"
            var password2 = "2222"
            val example1Size = "4x3"

            // 1. Request: POST /game
            // fail /game first - no auth - failure
            handleRequest(HttpMethod.Post, "/game") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                user1 = User(email = email1, password = password1)
                val json = Json.encodeToString(user1)
                setBody(json)

            }.apply {
                val ma: Map<String, String> = Json.decodeFromString<Map<String, String>>(response.content.toString())
                assertEquals(Status.AUTHORIZATION_FAILED.statusCode, response.status())
                assertEquals(Status.AUTHORIZATION_FAILED.message, ma["status"])

            }

            // 2. Request: POST /signup
            // signup Player 1
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                user1 = User(email = email1, password = password1)
                val json = Json.encodeToString(user1)
                setBody(json)
            }.apply {
                val expectedOnFirstSignup = Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
                assertEquals(expectedOnFirstSignup, response.content)
                assertEquals(1, info().num_users)

            }

            // 3. Request: POST /signup
            // signup Player 2
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                user2 = User(email = email2, password = password2)
                val json = Json.encodeToString(user2)
                setBody(json)

            }.apply {
                val expectedOnSecondSignup = Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
                assertEquals(expectedOnSecondSignup, response.content)
                assertEquals(2, info().num_users)
            }

            // 4. Request: POST /signin
            // signin Player 1
            handleRequest(HttpMethod.Post, "/signin") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(user1)
                setBody(json)

            }.apply {
                val bodyJson = response.content.toString()
                val playerSigninResponsePayload: PlayerSigninResponsePayload = Json.decodeFromString(bodyJson)
                user1.jwt = playerSigninResponsePayload.token
                assertEquals(Status.SIGNED_IN.message, playerSigninResponsePayload.status)
                assertEquals(1, UserSignedInStore.size)
                assertEquals(1, info().num_users_signin)
            }

            // 5. Request: POST /signin
            // signin Player 2
            handleRequest(HttpMethod.Post, "/signin") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(user2)
                setBody(json)

            }.apply {
                val playerSigninResponsePayload =
                    Json.decodeFromString<PlayerSigninResponsePayload>(response.content.toString())
                user2.jwt = playerSigninResponsePayload.token
                assertEquals(Status.SIGNED_IN.message, playerSigninResponsePayload.status)
                assertEquals(2, UserSignedInStore.size)
                assertEquals(2, info().num_users_signin)

            }

            // 6. Request: POST /game
            // successfully /game first
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
                    NewGameRequestPayload(player1 = user1.email, player2 = "", size = example1Size)
                val json = Json.encodeToString(ngr)
                setBody(json)
            }.apply {
                val bodyJsonHere = response.content.toString()
                val ngr: NewGameResponsePayload = Json.decodeFromString<NewGameResponsePayload>(bodyJsonHere)
                assertEquals(Status.NEW_GAME_STARTED.statusCode, response.status())
                assertEquals(Status.NEW_GAME_STARTED.message, ngr.status)
                assertEquals(1, ngr.gameId)
                assertEquals(example1Size, ngr.size)

            }


        }
    }

    @Test
    fun `signup two people`() {
        withTestApplication(Application::module) {
            // sign up the 1st user
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val email1 = "foo@bar.com"
                val password1 = "foobar"
                val user = User(email = email1, password = password1)
                val json = Json.encodeToString(user)
                setBody(json)

            }.apply {
                val expectedOnFirstSignup = Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
                assertEquals(expectedOnFirstSignup, response.content)
                assertEquals(1, info().num_users)
            }

            // sign up the 2nd user
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val email2 = "foo2@bar.com"
                val password2 = "foo2bar"
                val user = User(email = email2, password = password2)
                val json = Json.encodeToString(user)
                setBody(json)

            }.apply {
                val expectedOnFirstSignup = Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
                assertEquals(expectedOnFirstSignup, response.content)
                assertEquals(2, info().num_users)
            }

        }

    }

    @ParameterizedTest
    @ValueSource(strings = ["carl@example.com:1111", "mike@example.com:2222"])
    fun `signup no auth needed success and failure`(data: String) {
        val (email, password) = data.trim().split(":")
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val user = User(email = email, password = password)
                val json = Json.encodeToString(user)
                setBody(json)
            }
        }.apply {
            assertEquals(Status.SIGNED_UP.statusCode, response.status())
            val expectedOnFirstSignup = Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
            println(
                """
                ZOWWY
                ${Json.decodeFromString<Map<String, String>>(response.content.toString())["status"]}
                
            """.trimIndent()
            )
            assertEquals(
                expectedOnFirstSignup,
                response.content.toString()
            )
//            assertEquals(1, UserStore.size)


        }
    }

    @Test
    fun `game failed authorization`() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Post, "/game") {
                (environment.config as MapApplicationConfig).apply {
                    // https://ktor.io/docs/old/jwt.html#jwt-settings
                    put("jwt.secret", secretForJwt)
                    put("jwt.realm", "Access to game")
                }
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(
                    NewGameRequestPayload(
                        player1 = "Jed@Clampett.com",
                        player2 = "Wilma@Flintstone.com",
                        size = "4x3"
                    )
                )
                println(
                    """
                    ${this.uri}
                    json: $json
                """.trimIndent()
                )
                setBody(json)
            }

        }.apply {
            // check results
            assertEquals(Status.AUTHORIZATION_FAILED.statusCode, response.status())
            assertEquals(
                Status.AUTHORIZATION_FAILED.message,
                Json.decodeFromString<Map<String, String>>(response.content.toString())["status"]
            )
        }
    }

    @Test
    fun `game status failed authorization`() {
        withTestApplication(Application::module) {
            val game_id = 1
            handleRequest(HttpMethod.Get, "/game/$game_id/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader("Monkey", "Gorilla")
            }.apply {
                assertEquals(Status.AUTHORIZATION_FAILED.statusCode, response.status())
                assertEquals(
                    Json.encodeToString(mapOf("status" to Status.AUTHORIZATION_FAILED.message)),
                    response.content
                )

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