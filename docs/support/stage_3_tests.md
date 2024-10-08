# stage_3_tests | [main readme](../../readme.md)

* TicTacToeOnlineTest.kt from Tests view at 
>`Tic-Tac-Toe_Online -> Tic-Tac-Toe_Online-task -> test -> Tic-Tac-Toe_Online/task/test -> TicTacToeOnlineTest`
  * test1 - pass
  * test2 - pass
  * test3 - pass
  * test4 
  * test5 
  * test6 
  * test7 
  * test8 
```text
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import tictactoeonline.module
import org.hyperskill.hstest.dynamic.DynamicTest
import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.expect.Expectation.expect
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.util.regex.Pattern.compile;
import org.hyperskill.hstest.testing.expect.json.JsonChecker.*;


@Serializable
data class SigninResponse(val status: String, val token: String)

class TicTacToeOnlineTest : StageTest<Any>() {
    private val userOne: String = """ { "email":"alex@hyperskill.org", "password":"hs2023"} """
    private val userTwo: String = """ { "email":"mira@hyperskill.org", "password":"112233"} """

    @DynamicTest
    fun test1(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /signup")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Registration failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"", "password":""} """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /signup")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Registration failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"mike@hyperskill.org", "password":"1122"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /signin")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signin")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test2(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"alex@hyperskill.org", "password":"hs2023"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /signup")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"mira@hyperskill.org", "password":"112233"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /signup")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"alex@hyperskill.org", "password":"1234"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /signup")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Registration failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"alex@hyperskill.org", "password":"1234"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /signin")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signin")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"alex@hyperskill.org", "password":"hs2023"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /signin")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signin")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed In"))
                            .value("token", isString())
                    )
                    val resp: String = response.content!!
                    val jsonResponse = Json.decodeFromString<SigninResponse>(resp)
                    if (jsonResponse.token != "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8" &&
                        jsonResponse.token != "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.VQIBO0jQ8qW-308raJtSrvqufTEPDWcJyQsfwjnjTLQ"
                    ) {
                        result = CheckResult.wrong(
                            """
                        Invalid login token!
                        Expected eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8 or eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.VQIBO0jQ8qW-308raJtSrvqufTEPDWcJyQsfwjnjTLQ
                        Found: ${jsonResponse.token}
                        Route: /signin
                    """.trimIndent()
                        )
                        return@apply
                    }
                }

                handleRequest(HttpMethod.Post, "/signin") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(""" { "email":"mira@hyperskill.org", "password":"112233"} """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /signin")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signin")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed In"))
                            .value("token", isString())
                    )
                    val resp: String = response.content!!
                    val jsonResponse = Json.decodeFromString<SigninResponse>(resp)
                    if (jsonResponse.token != "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.Q5JRRLXBVRbu16BcvcQNUMj_WXrEmFDLPM5QZYA9DFA" &&
                        jsonResponse.token != "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    ) {
                        result = CheckResult.wrong(
                            """
                        Invalid login token!
                        Expected eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.Q5JRRLXBVRbu16BcvcQNUMj_WXrEmFDLPM5QZYA9DFA or eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q
                        Found: ${jsonResponse.token}
                        Route: /signin
                    """.trimIndent()
                        )
                        return@apply
                    }
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test3(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Unauthorized) {
                        result =
                            CheckResult.wrong("Expected status: 401 Unauthorized\nFound:${response.status()}\nRoute: /game")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

                handleRequest(HttpMethod.Get, "/games") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Unauthorized) {
                        result =
                            CheckResult.wrong("Expected status: 401 Unauthorized\nFound:${response.status()}\nRoute: /games")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /games")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Unauthorized) {
                        result =
                            CheckResult.wrong("Expected status: 401 Unauthorized\nFound:${response.status()}\nRoute: /game/1/join")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/join")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/1/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Unauthorized) {
                        result =
                            CheckResult.wrong("Expected status: 401 Unauthorized\nFound:${response.status()}\nRoute: /game/1/status")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/status")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody("{ }")
                }.apply {
                    if (response.status() != HttpStatusCode.Unauthorized) {
                        result =
                            CheckResult.wrong("Expected status: 401 Unauthorized\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Authorization failed"))
                    )
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test4(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Creating a game failed"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("status", compile("New game started"))
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile(""))
                            .value("size", compile("4x3"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(2))
                            .value("status", compile("New game started"))
                            .value("player1", compile(""))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("3x6"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isArray(2)
                            .item(
                                0,
                                isObject()
                                    .value("game_id", isInteger(1))
                                    .value("player1", compile("mira@hyperskill.org"))
                                    .value("player2", compile(""))
                                    .value("size", compile("4x3"))
                            )
                            .item(
                                1,
                                isObject()
                                    .value("game_id", isInteger(2))
                                    .value("player1", compile(""))
                                    .value("player2", compile("alex@hyperskill.org"))
                                    .value("size", compile("3x6"))
                            )
                    )
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test5(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("game_status", compile("game not started"))
                            .value(
                                "field",
                                isArray(4)
                                    .item(0, isArray(" ", " ", " "))
                                    .item(1, isArray(" ", " ", " "))
                                    .item(2, isArray(" ", " ", " "))
                                    .item(3, isArray(" ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile(""))
                            .value("size", compile("4x3"))
                    )
                }

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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Failed to get game status"))
                    )
                }


            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test6(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

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

                handleRequest(HttpMethod.Post, "/game/1/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/1/join")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/join")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Joining the game succeeded"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/1/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("game_status", compile("1st player's move"))
                            .value(
                                "field",
                                isArray(4)
                                    .item(0, isArray(" ", " ", " "))
                                    .item(1, isArray(" ", " ", " "))
                                    .item(2, isArray(" ", " ", " "))
                                    .item(3, isArray(" ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("4x3"))
                    )
                }


            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test7(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

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
                        "size": "1x10"
                    }
                """.trimIndent()
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }

                handleRequest(HttpMethod.Post, "/game/2/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(2,3)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("You have no rights to make this move"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(2,3)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Move done"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/1/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("game_status", compile("2nd player's move"))
                            .value(
                                "field",
                                isArray(4)
                                    .item(0, isArray(" ", " ", " "))
                                    .item(1, isArray(" ", " ", "X"))
                                    .item(2, isArray(" ", " ", " "))
                                    .item(3, isArray(" ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("4x3"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(2,3)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.BadRequest) {
                        result =
                            CheckResult.wrong("Expected status: 400 Bad Request\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Incorrect or impossible move"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(1,2)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Move done"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/1/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("game_status", compile("1st player's move"))
                            .value(
                                "field",
                                isArray(4)
                                    .item(0, isArray(" ", "O", " "))
                                    .item(1, isArray(" ", " ", "X"))
                                    .item(2, isArray(" ", " ", " "))
                                    .item(3, isArray(" ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("4x3"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(1,4)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/2/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/2/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Move done"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/2/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/2/status")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/2/status")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(2))
                            .value("game_status", compile("2nd player's move"))
                            .value(
                                "field",
                                isArray(1)
                                    .item(0, isArray(" ", " ", " ", "X", " ", " ", " ", " ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("1x10"))
                    )
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest
    fun test8(): CheckResult {
        var result: CheckResult = CheckResult.correct();
        try {
            withTestApplication(Application::module) {
                // create users as data is not persisted between tests for this stage
                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userOne)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

                handleRequest(HttpMethod.Post, "/signup") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(userTwo)
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\n" +
                                    "Found:${response.status()}\n" +
                                    "Route: /signup\n" +
                                    "Note: Data should not persist as the server restarts, store the users in regular variables.")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /signup")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("Signed Up"))
                    )
                }

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
                        "size": "1x10"
                    }
                """.trimIndent()
                    )
                }

                handleRequest(HttpMethod.Post, "/game/1/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }

                handleRequest(HttpMethod.Post, "/game/2/join") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }


                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(1,1)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(1,2)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(2,1)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(2,2)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(3,1)" } """)
                }

                handleRequest(HttpMethod.Post, "/game/1/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(3,2)" } """)
                }.apply {
                    if (response.status() != HttpStatusCode.Forbidden) {
                        result =
                            CheckResult.wrong("Expected status: 403 Forbidden\nFound:${response.status()}\nRoute: /game/1/move")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/1/move")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("status", compile("You have no rights to make this move"))
                    )
                }

                handleRequest(HttpMethod.Get, "/game/1/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
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
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(1))
                            .value("game_status", compile("1st player won"))
                            .value(
                                "field",
                                isArray(4)
                                    .item(0, isArray("X", "O", " "))
                                    .item(1, isArray("X", "O", " "))
                                    .item(2, isArray("X", " ", " "))
                                    .item(3, isArray(" ", " ", " "))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("4x3"))
                    )
                }

                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(1,1)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(1,10)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(1,2)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(1,9)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pcmFAaHlwZXJza2lsbC5vcmcifQ.5_G2rDHUYjJFzrqih0HXGuNTxxQMo6S5A0YFdFD9J8Q"
                    )
                    setBody(""" { "move": "(1,4)" } """)
                }
                handleRequest(HttpMethod.Post, "/game/2/move") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(""" { "move": "(1,8)" } """)
                }

                handleRequest(HttpMethod.Get, "/game/2/status") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    addHeader(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFsZXhAaHlwZXJza2lsbC5vcmcifQ.v1j3WkYqH1zb7vO6D7ylINhB47yp1HFrrmjYT8vwPO8"
                    )
                    setBody(" { } ")
                }.apply {
                    if (response.status() != HttpStatusCode.OK) {
                        result =
                            CheckResult.wrong("Expected status: 200 OK\nFound:${response.status()}\nRoute: /game/2/status")
                        return@apply
                    }
                    if (response.content.isNullOrBlank()) {
                        result = CheckResult.wrong("Empty response!\nRoute: /game/2/status")
                        return@apply
                    }
                    expect(response.content).asJson().check(
                        isObject()
                            .value("game_id", isInteger(2))
                            .value("game_status", compile("2nd player won"))
                            .value(
                                "field",
                                isArray(1)
                                    .item(0, isArray("X", "X", " ", "X", " ", " ", " ", "O", "O", "O"))
                            )
                            .value("player1", compile("mira@hyperskill.org"))
                            .value("player2", compile("alex@hyperskill.org"))
                            .value("size", compile("1x10"))
                    )
                }

            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

}
```