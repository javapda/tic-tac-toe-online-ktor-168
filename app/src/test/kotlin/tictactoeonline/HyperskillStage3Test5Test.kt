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

class HyperskillStage3Test5Test {
    private val userOne: String = """ { "email":"alex@hyperskill.org", "password":"hs2023"} """
    private val userTwo: String = """ { "email":"mira@hyperskill.org", "password":"112233"} """

    @Test
    fun test5() {
        APPLICATION_TESTING = true
        var result: CheckResult = CheckResult.correct()
        withTestApplication(Application::module) {
            // create users as data is not persisted between tests for this stage
            // sign up userOne
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
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                val ng = Json.decodeFromString<Map<String, String>>(response.content.toString())
                assertEquals(Status.SIGNED_UP.message, ng["status"])

            }

            // sign up userTwo
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
                val ng = Json.decodeFromString<Map<String, String>>(response.content.toString())
                assertEquals(Status.SIGNED_UP.message, ng["status"])
            }

            // new game requested by userOne : mira@hyperskill.org
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
            }

            // status request of game 1 by mira@hyperskill.org - Success
            handleRequest(HttpMethod.Get, "/game/1/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                )
                setBody(" { } ")
            }.apply {
                if (response.status() != HttpStatusCode.OK) {
                    result =
                        CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/1/status")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /game/1/status")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                val gameStatus: GameStatusResponsePayload =
                    Json.decodeFromString<GameStatusResponsePayload>(response.content.toString())
                with(gameStatus) {
                    assertEquals(1, gameId)
                    assertEquals("4x3", fieldDimensions)
                    assertEquals("game not started", status)
                    assertEquals("mira@hyperskill.org", playerXName ?: "")
                    assertEquals("", playerOName ?: "")
                    assertEquals(4, field2DArray!!.size)
                    assertEquals(listOf(" ", " ", " "), field2DArray!![0])
                    assertEquals(listOf(" ", " ", " "), field2DArray!![1])
                    assertEquals(listOf(" ", " ", " "), field2DArray!![2])
                    assertEquals(listOf(" ", " ", " "), field2DArray!![3])
                }

            }

            // status request of game 1 by user mira@hyperskill.org - Failure since mira is not a player on the game
            handleRequest(HttpMethod.Get, "/game/1/status") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(
                    HttpHeaders.Authorization,
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                )
                setBody(" { } ")
            }.apply {
                if (response.status() != HttpStatusCode.Forbidden) {
                    result =
                        CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /game/1/status")
                    return@apply
                }
                if (response.content.isNullOrBlank()) {
                    result = CheckResult.wrong("Empty response!\nRoute: /game/1/status")
                    return@apply
                }
                assertTrue(MyJsonTools.isJsonObject(response.content.toString()))
                val map = Json.decodeFromString<Map<String, String>>(response.content.toString())
                assertTrue(map.containsKey("status"))
                assertEquals(Status.GET_STATUS_FAILED.message, map["status"])
//                expect(response.content).asJson().check(
//                    isObject()
//                        .value("status", compile("Failed to get game status"))
//                )
            }

        }
    }
}