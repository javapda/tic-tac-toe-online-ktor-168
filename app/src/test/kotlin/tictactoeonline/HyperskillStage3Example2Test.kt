package tictactoeonline

import com.auth0.jwt.JWT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HyperskillStage3Example2Test {
    fun emailFromJwt(jwt: String) =
        JWT.require(algorithm).build().verify(jwt).getClaim("email").asString()

    @BeforeEach
    fun setup() {
        clearAll()
    }

    @Test
    fun `Example 2`() {

        withTestApplication(Application::module) {
            lateinit var user1: User
            val example2Size = "3x3"

            // 1. Request: POST /signup
            // signup Artem without password - failure
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                user1 = User(email = emailArtem, password = "")
                val json = Json.encodeToString(user1)
                setBody(json)
            }.apply {
                val expectedOnFirstSignup = Json.encodeToString(mapOf("status" to Status.REGISTRATION_FAILED.message))
                assertEquals(expectedOnFirstSignup, response.content.toString())
                assertEquals(0, info().num_users)

            }

            // 2. Request: POST /signup
            // signup Artem with email + password - Success
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                user1 = User(email = emailArtem, password = "1234")
                val json = Json.encodeToString(user1)
                setBody(json)
            }.apply {
                assertEquals(Status.SIGNED_UP.statusCode, response.status())
                assertEquals(
                    Status.SIGNED_UP.message,
                    Json.decodeFromString<Map<String, String>>(response.content.toString())["status"]
                )
                assertEquals(1, info().num_users)
            }

            // 3. Request: POST /signin
            // signin Artem with incorrect password
            handleRequest(HttpMethod.Post, "/signin") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(user1)
                setBody(json)
            }.apply {
                val playerSigninResponsePayload =
                    Json.decodeFromString<PlayerSigninResponsePayload>(response.content.toString())
                user1.jwt = playerSigninResponsePayload.token
                assertEquals(Status.SIGNED_IN.statusCode, response.status())
                assertEquals(Status.SIGNED_IN.message, playerSigninResponsePayload.status)
                assertEquals(1, UserSignedInStore.size)
                assertEquals(1, info().num_users_signin)

            }

            // 4. Request: POST /signin
            // signing Artem (Player1)
            handleRequest(HttpMethod.Post, "/signin") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(user1)
                setBody(json)
            }.apply {
                val playerSigninResponsePayload =
                    Json.decodeFromString<PlayerSigninResponsePayload>(response.content.toString())
                assertEquals(Status.SIGNED_IN.message, playerSigninResponsePayload.status)
                assertEquals(1, UserSignedInStore.size)
                assertEquals(1, info().num_users_signin)
            }

            // 5. Request: POST /game
            // auth Artem start a game as Player2
            handleRequest(HttpMethod.Post, "/game") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")

                val email = emailFromJwt(user1.jwt!!)
                val ngr: NewGameRequestPayload =
                    NewGameRequestPayload(player1 = "", player2 = user1.email, size = example2Size)
                val json = Json.encodeToString(ngr)
                setBody(json)
            }.apply {
                val ngr: NewGameResponsePayload =
                    Json.decodeFromString<NewGameResponsePayload>(response.content.toString())
                assertEquals(Status.NEW_GAME_STARTED.statusCode, response.status())
                assertEquals(Status.NEW_GAME_STARTED.message, ngr.status)
                assertEquals(1, ngr.gameId)
                assertEquals(example2Size, ngr.size)
            }

            // 6. Request: POST /game/1/join
            // auth join by Artem - Success
            handleRequest(HttpMethod.Post, "/game/1/join") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
            }.apply {
                val bodyDataMap = Json.decodeFromString<Map<String, String>>(response.content.toString())
                assertEquals(Status.JOINING_GAME_SUCCEEDED.statusCode, response.status())
                assertTrue(bodyDataMap.containsKey("status"))
                assertEquals(Status.JOINING_GAME_SUCCEEDED.message, bodyDataMap["status"])
            }

            // 7. Request: POST /game/1/move
            // auth Artem (who will be playing as both Player1 and Player2) move (1,1) - Success
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(1,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                assertEquals(
                    Status.MOVE_DONE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )
            }

            // 8. Request: POST /game/1/move
            // auth Artem (who will be playing as both Player1 and Player2) move (1,2) - Success
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(1,2)"))
                setBody(json)

            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                assertEquals(
                    Status.MOVE_DONE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )

            }

            // 9. Request: POST /game/1/move
            // auth Artem (who will be playing as both Player1 and Player2) move (2,1) - Success
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(2,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                assertEquals(
                    Status.MOVE_DONE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )
            }

            // 10. Request: POST /game/1/move
            // auth Artem (who will be playing as both Player1 and Player2) move (2,2) - Success
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(2,2)"))
                setBody(json)
            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                assertEquals(
                    Status.MOVE_DONE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )
            }

            // 11. Request: POST /game/1/move
            // auth Artem (who will be playing as both Player1 and Player2) move (3,1) - Success
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(3,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                assertEquals(
                    Status.MOVE_DONE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )
            }

            // 12. Request: GET /game/1/status
            // auth status - Success
            handleRequest(HttpMethod.Get, "/game/1/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
            }.apply {
                assertEquals(Status.GET_STATUS_SUCCEEDED.statusCode, response.status())
                assertEquals(
                    "1st player won",
                    Json.decodeFromString<GameStatusResponsePayload>(response.content.toString()).status
                )
            }

        }
    }
}