package tictactoeonline

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class Status(val message: String, val statusCode: HttpStatusCode) {
    SIGNED_IN("Signed In", HttpStatusCode.OK), // with JWT
    SIGNED_UP("Signed Up", HttpStatusCode.OK),
    AUTHORIZATION_FAILED("Authorization failed", HttpStatusCode.Unauthorized),
    INCORRECT_OR_IMPOSSIBLE_MOVE("Incorrect or impossible move", HttpStatusCode.BadRequest),
    NO_RIGHTS_TO_MOVE("You have no rights to make this move", HttpStatusCode.Forbidden),
    REGISTRATION_FAILED("Registration failed", HttpStatusCode.Forbidden),
    NEW_GAME_STARTED("New game started", HttpStatusCode.OK),
    CREATING_GAME_FAILED("Creating a game failed", HttpStatusCode.Forbidden),
    JOINING_GAME_SUCCEEDED("Joining the game succeeded", HttpStatusCode.OK),
    JOINING_GAME_FAILED("Joining the game failed", HttpStatusCode.Forbidden),
    GET_STATUS_FAILED("Failed to get game status", HttpStatusCode.Forbidden),
    GET_STATUS_SUCCEEDED(
        "Succeeded in getting game status - NOTE: this is not an official response",
        HttpStatusCode.OK
    ),
    MOVE_REQUEST_WITHOUT_AUTHORIZATION("Authorization failed", HttpStatusCode.Forbidden),
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
    @SerialName("game_id") val gameId: String,
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
        val verbose = (environment.config.propertyOrNull("ktor.mycustom.verbose")?.getString() ?: "false").toBoolean()
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
        get("/helloWorld") {
            call.respondText("Hello, World!")
        }

        get("/help") {
            call.respond(help())
        }

        get("/info") {
            call.respond(Json.encodeToString(info(call)))
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


