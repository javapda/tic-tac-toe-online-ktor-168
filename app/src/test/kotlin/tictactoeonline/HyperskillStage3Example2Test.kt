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
//            lateinit var user2: User
//            lateinit var response: HttpResponse
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


        }
    }
}