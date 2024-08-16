package tictactoeonline

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

enum class Status(val message: String, val statusCode: HttpStatusCode) {
    SIGNED_IN("Signed In", HttpStatusCode.OK), // with JWT
    SIGNED_IN_FAILED("Authorization failed", HttpStatusCode.Forbidden), // with JWT
    SIGNED_UP("Signed Up", HttpStatusCode.OK),
    AUTHORIZATION_FAILED("Authorization failed", HttpStatusCode.Unauthorized),
    INCORRECT_OR_IMPOSSIBLE_MOVE("Incorrect or impossible move", HttpStatusCode.BadRequest),
    NO_RIGHTS_TO_MOVE("You have no rights to make this move", HttpStatusCode.Forbidden),
    REGISTRATION_FAILED("Registration failed", HttpStatusCode.Forbidden),
    NEW_GAME_STARTED("New game started", HttpStatusCode.OK),
    NEW_GAME_CREATION_FAILED("Creating a game failed", HttpStatusCode.Forbidden),
    CREATING_GAME_FAILED("Creating a game failed", HttpStatusCode.Forbidden),
    JOINING_GAME_SUCCEEDED("Joining the game succeeded", HttpStatusCode.OK),
    JOINING_GAME_FAILED("Joining the game failed", HttpStatusCode.Forbidden),
    GET_STATUS_FAILED("Failed to get game status", HttpStatusCode.Forbidden),
    GET_STATUS_SUCCEEDED(
        "Succeeded in getting game status - NOTE: this is not an official response",
        HttpStatusCode.OK
    ),
    MOVE_REQUEST_WITHOUT_AUTHORIZATION("Authorization failed", HttpStatusCode.Unauthorized),

    //    MOVE_REQUEST_WITHOUT_AUTHORIZATION("Authorization failed", HttpStatusCode.Forbidden),
    MOVE_DONE("Move done", HttpStatusCode.OK),
}

@Serializable
data class User(val email: String, val password: String) {
    var jwt: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return email == other.email
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}

@Serializable
data class PlayerSignupRequestPayload(val email: String, val password: String)

@Serializable
data class PlayerSigninResponsePayload(val status: String, val token: String)

@Serializable
data class NewGameRequestPayload(val player1: String, val player2: String, val size: String) {
    enum class NewGameRequestPayloadError(description: String) {
        BOTH_PLAYER_MISSING_EMAIL_ADDRESS("both player missing email address"),
        BOTH_PLAYER_EMAIL_ADDRESSES_PRESENT("both player email address present, should only have one"),
        INVALID_FIELD_DIMENSIONS_PROVIDED("invalid field dimensions provided")
    }

    fun isValid(): Boolean {
        return ((player1.isNotEmpty() && player2.isEmpty()) ||
                (player1.isEmpty() && player2.isNotEmpty()))
                && PlayingGrid.isValidFieldDimensionString(size)
    }

    fun isInvalid() = !isValid()
    fun whyInvalid(): Set<NewGameRequestPayloadError> {
        if (isValid()) {
            throw Exception("I am valid: $this")
        }
        val errors = mutableSetOf<NewGameRequestPayloadError>()
        if (player1.isEmpty() && player2.isEmpty()) errors.add(NewGameRequestPayloadError.BOTH_PLAYER_MISSING_EMAIL_ADDRESS)
        if (player1.isNotEmpty() && player2.isNotEmpty()) errors.add(NewGameRequestPayloadError.BOTH_PLAYER_EMAIL_ADDRESSES_PRESENT)
        if (!PlayingGrid.isValidFieldDimensionString(size)) errors.add(NewGameRequestPayloadError.INVALID_FIELD_DIMENSIONS_PROVIDED)
        return errors.toSet()
    }

}

@Serializable
data class NewGameResponsePayload(
    @SerialName("status") val status: String,
    val player1: String,
    val player2: String,
    val size: String,
    @SerialName("game_id") val gameId: Int
)


@Serializable
data class PlayerMoveRequestPayload(val move: String)


@Serializable
data class PlayerMoveResponsePayload(@SerialName("status") val status: String)

@Serializable
data class GameStatusOnlyResponsePayload(@SerialName("game_status") val status: String)

@Serializable
data class GameStatusResponsePayload(
    @SerialName("game_id") val gameId: Int,
    @SerialName("game_status") val status: String,
    @SerialName("field") val field2DArray: List<List<String>>? = null,
    @SerialName("player1") val playerXName: String? = null,
    @SerialName("player2") val playerOName: String? = null,
    @SerialName("size") val fieldDimensions: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameStatusResponsePayload

        if (status != other.status) return false
        if (field2DArray != other.field2DArray) return false
        if (playerXName != other.playerXName) return false
        if (playerOName != other.playerOName) return false
        if (fieldDimensions != other.fieldDimensions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (field2DArray?.hashCode() ?: 0)
        result = 31 * result + (playerXName?.hashCode() ?: 0)
        result = 31 * result + (playerOName?.hashCode() ?: 0)
        result = 31 * result + (fieldDimensions?.hashCode() ?: 0)
        return result
    }
}

@Serializable
data class GamesResponsePayload(
    @SerialName("game_id") val gameId: Int,
    @SerialName("player1") val playerXName: String? = null,
    @SerialName("player2") val playerOName: String? = null,
    @SerialName("size") val fieldDimensions: String? = null
)

@Serializable
data class InfoPayload(
    val num_users: Int = UserStore.size,
    val uri: String,
    val port: Int,
    val http_method: String,
    val num_parameters: Int,
    val users: MutableList<User>,
    val num_users_signin: Int = UserSignedInStore.size,
    val users_signin: MutableList<User>,
)

@Serializable
data class Endpoint(
    val path: String,
    val method: String,
    val auth_required: Boolean,
    val description: String = "",
)

@Serializable
data class HelpPayload(val endpoints: List<Endpoint>)

val UserStore: MutableList<User> = mutableListOf()
val UserSignedInStore: MutableList<User> = mutableListOf()
val GameStore: MutableList<Game> = mutableListOf()

fun clearAll() {
    GameStore.clear()
    UserSignedInStore.clear()
    UserStore.clear()
}


fun Application.configureRouting() {
    if (false) {
        val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: 8383
        val verbose = (environment.config.propertyOrNull("mycustom.verbose")?.getString() ?: "false").toBoolean()
        println(
            """
        ${"$-".repeat(20)}
        configureRouting
        port: $port
        verbose: $verbose
        ${"$-".repeat(20)}
    """.trimIndent()
        )
    }



    routing {

        authenticate("auth-jwt") {

            post("/uploadSecure") {
                var fileDescription: String
                var fileName: String
                var fileUploadDestination: File = File(".")
                // https://ktor.io/docs/old/requests.html#form_data
                val multipartData = call.receiveMultipart()
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            fileDescription = part.value
                        }

                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            var fileBytes = part.streamProvider().readBytes()
                            val destinationDirectory = File("build/uploads")
                            if (!destinationDirectory.exists()) {
                                destinationDirectory.mkdirs()
                            }
                            fileUploadDestination = File(destinationDirectory, fileName)
                            fileUploadDestination.writeBytes(fileBytes)
                        }

                        is PartData.BinaryItem -> {
                            println("Not doing anything with Binary Item")
                            println("Binary Item name: ${part.name}")
                        }

                        else -> {
                            throw Exception("DONT KNOW WHAT TYPE OF PART IT IS? part=${part}")
                        }
                    }
                }
                println(
                    """
                ${"=-".repeat(50)}
                ${call.request.uri}
                multipartData: $multipartData
                ${"=-".repeat(50)}
            """.trimIndent()
                )
                call.respondText("POST: upload, ${call.request.uri}, uploaded to ${fileUploadDestination.absoluteFile}")
            }

            fun ApplicationCall.playerEmail(): String {
                return this.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
            }
            post("/game") {
                val authHeader = call.request.headers[HttpHeaders.Authorization]
                val principal = call.principal<JWTPrincipal>()
                val playerEmailAddress = call.playerEmail()
                val newGameRequestPayload = call.receive<NewGameRequestPayload>()
                if (newGameRequestPayload.player1 != playerEmailAddress && newGameRequestPayload.player2 != playerEmailAddress) {
                    call.respond(
                        Status.NEW_GAME_CREATION_FAILED.statusCode,
                        mapOf("status" to Status.NEW_GAME_CREATION_FAILED.message)
                    )
                    return@post
                }
                require(UserStore.any { user -> user.email == playerEmailAddress })
                val user = UserStore.find { user -> user.email == playerEmailAddress }
                val newGame = TicTacToeOnline()
                newGame.initializeField(newGameRequestPayload.size)
                GameStore.add(newGame)
                val game_id = GameStore.indexOf(newGame) + 1
                if (newGameRequestPayload.isInvalid()) {
                    throw Exception(newGameRequestPayload.whyInvalid().toString())
                }
                var player1 = ""
                var player2 = ""
                if (newGameRequestPayload.player1.isNotEmpty()) {
                    newGame.playerX = Player(name = newGameRequestPayload.player1, marker = 'X')
                    player1 = newGameRequestPayload.player1
                    newGameRequestPayload.player1
                } else {
                    newGame.playerO = Player(name = newGameRequestPayload.player2, marker = 'O')
                    player2 = newGameRequestPayload.player2
                    newGameRequestPayload.player2
                }

                val respPayload = NewGameResponsePayload(
                    status = Status.NEW_GAME_STARTED.message,
                    player1 = player1,
                    player2 = player2,
                    gameId = game_id,
                    size = newGameRequestPayload.size
                )
                call.respond(respPayload)
            }

            /**
             * Join a game
             */
            post("/game/{game_id}/join") {
                call.parameters["game_id"]?.let { stringId ->
                    val playerEmail = call.playerEmail()
                    stringId.toIntOrNull()?.let { game_id ->
                        if (game_id in 0 until GameStore.lastIndex) {
                            call.respond(
                                Status.JOINING_GAME_FAILED.statusCode,
                                mapOf("status" to Status.JOINING_GAME_FAILED.message)
                            )
                        } else {
                            val game = GameStore[game_id - 1]
                            val ttt = game as TicTacToeOnline
                            // need utility to get player from JWT
                            val user = UserStore.find { user -> user.email == playerEmail }

                            ttt.addPlayer(Player(name = user?.email ?: "BOGUS-EMAIL", marker = 'X'))

                            @Serializable
                            data class StatusPayload(val status: String = Status.JOINING_GAME_SUCCEEDED.message)
                            call.respond(Status.JOINING_GAME_SUCCEEDED.statusCode, StatusPayload())
                        }
                    }
                }
            }

            /**
             * make a move
             */
            post("/game/{game_id}/move") {
                call.parameters["game_id"]?.let { stringId ->
                    stringId.toIntOrNull()?.let { game_id ->
                        val playerEmail = call.playerEmail()
                        val game = GameStore[game_id - 1]
                        val ttt = game as TicTacToeOnline
                        val playerMoveRequestPayload = call.receive<PlayerMoveRequestPayload>()
                        val move = playerMoveRequestPayload.move

                        if (ttt.currentPlayer.name != playerEmail) {
                            // if it's not your turn, then you have no right
                            call.respond(
                                Status.NO_RIGHTS_TO_MOVE.statusCode,
                                PlayerMoveResponsePayload(Status.NO_RIGHTS_TO_MOVE.message)
                            )
                        } else if (ttt.isValidMove(move) && ttt.isOccupied(move)) {
                            // fail, move
                            call.respond(
                                Status.INCORRECT_OR_IMPOSSIBLE_MOVE.statusCode,
                                PlayerMoveResponsePayload(Status.INCORRECT_OR_IMPOSSIBLE_MOVE.message)
                            )
                        } else if (ttt.isValidMove(move) && ttt.move(move)) {
                            // success
                            call.respond(
                                Status.MOVE_DONE.statusCode,
                                PlayerMoveResponsePayload(Status.MOVE_DONE.message)
                            )
                        } else {
                            call.respond(
                                Status.NO_RIGHTS_TO_MOVE.statusCode,
                                PlayerMoveResponsePayload(Status.NO_RIGHTS_TO_MOVE.message)
                            )
                        }
                    }
                }
            }

            get("/game/{game_id}/status") {
                /**
                 * NOTE: only player's participating in a game have access to the game status,
                 * otherwise it is a fail
                 */
                call.parameters["game_id"]?.let { stringId ->
                    stringId.toIntOrNull()?.let { game_id ->
                        val game = GameStore[game_id - 1]
                        val ttt = game as TicTacToeOnline
                        val playerEmail = call.playerEmail()
                        if (ttt.playerXName() != playerEmail && ttt.playerOName() != playerEmail) {
                            call.respond(
                                Status.GET_STATUS_FAILED.statusCode,
                                mapOf("status" to Status.GET_STATUS_FAILED.message)
                            )
                            return@get
                        }
                        val gsrp = GameStatusResponsePayload(
                            gameId = (GameStore.indexOf(game) + 1),
                            status = ttt.state.description,
                            field2DArray = ttt.renderFieldTo2DArray(),
                            playerXName = ttt.playerXName(),
                            playerOName = ttt.playerOName(),
                            fieldDimensions = ttt.fieldSize(),
                        )
                        call.respond(Status.GET_STATUS_SUCCEEDED.statusCode, gsrp)
//                        games[game_id]?.let { user ->
//                            call.respondText(user)
//                        }
                    }
                }
            }

            get("/games") {
                val gamesResponses = mutableListOf<GamesResponsePayload>()
                GameStore.mapIndexed { idx, game ->
                    if (game is TicTacToeOnline) {
                        gamesResponses.add(
                            with(game) {
                                GamesResponsePayload(
                                    gameId = idx + 1,
                                    playerXName = game.playerXName(),
                                    playerOName = game.playerOName(),
                                    fieldDimensions = game.fieldSize()
                                )
                            })
                    } else {
                        throw Exception("Unknown game type, only know about TicTacToeOnline")
                    }
                }
                call.respond(gamesResponses.toList())
            }

        }

        route("/") {


            post("/signup") {
                val json = call.receiveText()
                var ng: PlayerSignupRequestPayload? = null
                try {
                    ng = Json.decodeFromString<PlayerSignupRequestPayload>(json)
                } catch (e: Exception) {
//                    call.respond(HttpStatusCode.Forbidden, Status.REGISTRATION_FAILED.message+"MONKEY")
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Json.encodeToString(mapOf("status" to Status.REGISTRATION_FAILED.message))
                    )

                    return@post
                }
                call.application.environment.log.info(ng.toString())
                val user = User(email = ng.email, password = ng.password)
                if (user.email.isEmpty() || user.password.isEmpty() || UserStore.contains(user)) {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        Json.encodeToString(mapOf("status" to Status.REGISTRATION_FAILED.message))
                    )
                } else {
                    UserStore.add(user)
                    call.respondText(Json.encodeToString(mapOf("status" to "Signed Up")))
                }
            }

            post("/signin") {
                val json = call.receiveText()
                val ng = Json { ignoreUnknownKeys = true }.decodeFromString<PlayerSignupRequestPayload>(json)
                call.application.environment.log.info(ng.toString())
                val user = User(email = ng.email, password = ng.password)
                call.application.environment.log.info(
                    """
                $json
            """.trimIndent()
                )
                // only signin users we know about and have a matching password
                if (UserStore.contains(user) && UserStore.find { storedUser -> user == storedUser }?.password == ng.password) {
                    // good
                    val secret = "ut920BwH09AOEDx5"
                    val token = JWT.create()
                        .withClaim("email", user.email)
                        .sign(Algorithm.HMAC256(secret))
                    user.jwt = token
                    if (!UserSignedInStore.contains(user)) {
                        UserSignedInStore.add(user)
                    }
                    call.respond(mapOf("status" to Status.SIGNED_IN.message, "token" to token))
                } else {
                    call.respond(
                        Status.SIGNED_IN_FAILED.statusCode,
//                        HttpStatusCode.Forbidden,
                        Json.encodeToString(mapOf("status" to Status.SIGNED_IN_FAILED.message))
                    )
//                    call.respond(
//                        HttpStatusCode.Unauthorized,
//                        Json.encodeToString(mapOf("status" to Status.SIGNED_UP.message))
//                    )
                }

            }

            get("") {
                call.respondText("Welcome to Tic-Tac-Toe Online")
            }

            // no auth required
            post("/upload") {
                var fileDescription: String
                var fileName: String
                var fileUploadDestination: File = File(".")
                // https://ktor.io/docs/old/requests.html#form_data
                val multipartData = call.receiveMultipart()
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            fileDescription = part.value
                        }

                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            var fileBytes = part.streamProvider().readBytes()
                            val destinationDirectory = File("build/uploads")
                            if (!destinationDirectory.exists()) {
                                destinationDirectory.mkdirs()
                            }
                            fileUploadDestination = File(destinationDirectory, fileName)
                            fileUploadDestination.writeBytes(fileBytes)
                        }

                        is PartData.BinaryItem -> {
                            println("Not doing anything with Binary Item")
                            println("Binary Item name: ${part.name}")
                        }

                        else -> {
                            throw Exception("DONT KNOW WHAT TYPE OF PART IT IS? part=${part}")
                        }
                    }
                }
                println(
                    """
                ${"=-".repeat(50)}
                ${call.request.uri}
                multipartData: $multipartData
                ${"=-".repeat(50)}
            """.trimIndent()
                )
                call.respondText("POST: upload, ${call.request.uri}, uploaded to ${fileUploadDestination.absoluteFile}")
            }
            get("/helloWorld") {
                call.respondText("Hello, World!")
            }
            post("/helloWorld") {
                call.respondText("POST: Hello, World!")
            }

            get("/help") {
                call.respond(Json.encodeToString(help()))
            }

            get("/info") {
                call.respond(Json.encodeToString(info(call)))
            }
        }

    }
}


fun info(call: ApplicationCall? = null): InfoPayload {

    return InfoPayload(
        num_users = UserStore.size,
        users = UserStore,
        uri = call?.request?.uri ?: "no-call",
        port = call?.request?.port() ?: -1,
        http_method = call?.request?.httpMethod?.toString() ?: "no-call",
        num_parameters = call?.parameters?.entries()?.size ?: -1,
        num_users_signin = UserSignedInStore.size,
        users_signin = UserSignedInStore
    )
}

fun help(): HelpPayload =
    HelpPayload(
        endpoints = listOf(
            Endpoint("/info", method = HttpMethod.Get.value, description = "show information", auth_required = false),
            Endpoint("/help", method = HttpMethod.Get.value, description = "show this help", auth_required = false),
            Endpoint(
                "/clearAll",
                method = HttpMethod.Delete.value,
                description = "clear all user, signin, and game data", auth_required = false
            ),
            Endpoint(
                "/signup",
                method = HttpMethod.Post.value,
                description = "register an email address with the website", auth_required = false
            ),
            Endpoint(
                "/signin",
                method = HttpMethod.Post.value,
                description = "let the system know you are online",
                auth_required = false
            ),
            Endpoint(
                "/helloWorld",
                method = HttpMethod.Get.value,
                description = "return Hello, World!",
                auth_required = false
            ),
            Endpoint(
                "/game",
                method = HttpMethod.Post.value,
                description = "used to request a new game",
                auth_required = true
            ),
            Endpoint(
                "/games",
                method = HttpMethod.Get.value,
                description = "getting a list of all games (game rooms)",
                auth_required = true
            ),
            Endpoint(
                "/game/1/join",
                method = HttpMethod.Post.value,
                description = "join a pre-existing game",
                auth_required = true
            ),
            Endpoint("/game/1/move", method = HttpMethod.Post.value, description = "", auth_required = true),
            Endpoint(
                "/game/1/status",
                method = HttpMethod.Get.value,
                description = "show status of a game with game_id=1",
                auth_required = true
            ),
        )
    )


