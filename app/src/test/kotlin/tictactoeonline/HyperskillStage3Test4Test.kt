package tictactoeonline

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import tictactoeonline.util.MyJsonTools
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HyperskillStage3Test4Test {
    private val userOne: String = """ { "email":"alex@hyperskill.org", "password":"hs2023"} """
    private val userTwo: String = """ { "email":"mira@hyperskill.org", "password":"112233"} """


    @Test
    fun test4() {
        APPLICATION_TESTING = true
        var result: CheckResult = CheckResult.correct()
        withTestApplication(Application::module) {

            // signup userOne : alex
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(userOne)
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong(
                            "Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables."
                        )
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /signup")
                    return@apply
                }

                // does the content look like json? text with brackets?
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                assertEquals(
                    Status.SIGNED_UP.message,
                    MyJsonTools.jsonObjectGetKeyOrNull(response.content.toString(), "status")
                )
            }

            // signup userTwo : mira
            handleRequest(HttpMethod.Post, "/signup") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(userTwo)
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong(
                            "Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables."
                        )
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /signup")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                assertEquals(
                    Status.SIGNED_UP.message,
                    MyJsonTools.jsonObjectGetKeyOrNull(response.content.toString(), "status")
                )
            }

            // start a new game with wrong/un-matching player1 carl@example.com does not match mira@hyperskill.org
            handleRequest(HttpMethod.Post, "/game") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                )
                setBody(
                    """
                    {
                        "player1": "carl@example.com",
                        "player2": "",
                        "size": "4x3"
                    }
                """.trimIndent()
                )
            }.apply {
                if (response.status() != HttpStatusCode.Forbidden) {
                    result =
                        CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /game")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /game")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                assertEquals(
                    Status.NEW_GAME_CREATION_FAILED.message,
                    MyJsonTools.jsonObjectGetKeyOrNull(response.content.toString(), "status")
                )

            }

            // start a new game
            handleRequest(HttpMethod.Post, "/game") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                )
                setBody(
                    """
                    {
                        "player1": "mira@hyperskill.org",
                        "player2": "",
                        "size": "4x3"
                    }
                """.trimIndent()
                )
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /game")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                val ng = Json.decodeFromString<NewGameResponsePayload>(response.content.toString())
                assertEquals(Status.NEW_GAME_STARTED.message, ng.status)
                assertEquals(1, ng.gameId)
                assertEquals("mira@hyperskill.org", ng.player1)
                assertEquals("", ng.player2)
                assertEquals("4x3", ng.size)

            }

            // start another new game
            handleRequest(HttpMethod.Post, "/game") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                )
                setBody(
                    """
                    {
                        "player1": "",
                        "player2": "alex@hyperskill.org",
                        "size": "3x6"
                    }
                """.trimIndent()
                )
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /game")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                val ng = Json.decodeFromString<NewGameResponsePayload>(response.content.toString())
                assertEquals(2, ng.gameId)
                assertEquals(Status.NEW_GAME_STARTED.message, ng.status)
                assertEquals("", ng.player1)
                assertEquals("alex@hyperskill.org", ng.player2)
                assertEquals("3x6", ng.size)

            }

            // games report as userOne alex@hyperskill.org
            handleRequest(HttpMethod.Get, "/games") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                )
                setBody(" { } ")
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /games")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /games")
                    return@apply
                }
                println(
                    """
                    ${response.content}
                """.trimIndent()
                )
                assertTrue(MyJsonTools.isJsonArray(response.content.toString()))
                val ng = Json.decodeFromString<List<GamesResponsePayload>>(response.content.toString())
                assertEquals(2, ng.size)
                with(ng[0]) {
                    assertEquals(1, gameId)
                    assertEquals("mira@hyperskill.org", playerXName)
                    assertEquals("", playerOName)
                    assertEquals("4x3", fieldDimensions)
                }
                with(ng[1]) {
                    assertEquals(2, gameId)
                    assertEquals("", playerXName)
                    assertEquals("alex@hyperskill.org", playerOName)
                    assertEquals("3x6", fieldDimensions)
                }

            }
        }
    }

}