package tictactoeonline

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
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HyperskillStage3Example1Test {
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

            // 7. Request: POST /game/1/join
            // auth join by Player2 (user2) - Success
            handleRequest(HttpMethod.Post, "/game/1/join") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user2.jwt}")
            }.apply {
                val bodyDataMap: Map<String, String> = Json.decodeFromString(response.content.toString())
                assertEquals(Status.JOINING_GAME_SUCCEEDED.statusCode, response.status())
                assertTrue(bodyDataMap.containsKey("status"))
                assertEquals(Status.JOINING_GAME_SUCCEEDED.message, bodyDataMap["status"])
            }

            // 8. Request: GET /game/1/status
            handleRequest(HttpMethod.Get, "/game/1/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
            }.apply {
                assertEquals(Status.GET_STATUS_SUCCEEDED.statusCode, response.status())
                val gameStatusResponsePayload =
                    Json.decodeFromString<GameStatusResponsePayload>(response.content.toString())
                assertEquals("1st player's move", gameStatusResponsePayload.status)
            }

            // 9. Request: POST /game/1/move
            // 1st move by Carl Player1 - successful move to (1,1)
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                setBody(Json.encodeToString(PlayerMoveRequestPayload("(1,1)")))

            }.apply {
                assertEquals(Status.MOVE_DONE.statusCode, response.status())
                val moveResponse = Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString())
                assertEquals(Status.MOVE_DONE.message, moveResponse.status)
            }

            // 10. Request: POST /game/1/move
            // move request without authorization header, failure, 401 Unauthorized
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                val json = Json.encodeToString(PlayerMoveRequestPayload("(1,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.MOVE_REQUEST_WITHOUT_AUTHORIZATION.statusCode, response.status())
                val moveResponseWithoutAuth =
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString())
                assertEquals(Status.MOVE_REQUEST_WITHOUT_AUTHORIZATION.message, moveResponseWithoutAuth.status)
            }

            // 11. Request: POST /game/1/move
            // at this point a move to (1,1) has already been done
            // here, we send the JWT for user : carl@example.com, but it is the same move and space already taken
            // or maybe, it is not Carl's turn (he did the last move) - it should be Mike's (Player2's) turn
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user1.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(1,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.NO_RIGHTS_TO_MOVE.statusCode, response.status())
                val moveResponseWithoutAuthAgain =
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString())
                assertEquals(Status.NO_RIGHTS_TO_MOVE.message, moveResponseWithoutAuthAgain.status)
            }

            // 12. Request: POST /game/1/move
            // authorized request move by mike (Player2) to an occupied place (1,1)
            // result will be a failure, 400 Bad Request,  "status": "Incorrect or impossible move"
            handleRequest(HttpMethod.Post, "/game/1/move") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Authorization, "Bearer ${user2.jwt}")
                val json = Json.encodeToString(PlayerMoveRequestPayload("(1,1)"))
                setBody(json)
            }.apply {
                assertEquals(Status.INCORRECT_OR_IMPOSSIBLE_MOVE.statusCode, response.status())
                assertEquals(
                    Status.INCORRECT_OR_IMPOSSIBLE_MOVE.message,
                    Json.decodeFromString<PlayerMoveResponsePayload>(response.content.toString()).status
                )
            }

        }


    }

}