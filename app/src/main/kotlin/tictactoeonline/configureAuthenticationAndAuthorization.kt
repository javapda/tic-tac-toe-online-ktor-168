package tictactoeonline

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*

fun Application.configureAuthenticationAndAuthorization() {
    // Configuring JWT authorization
    val secretFromConfig = environment.config.propertyOrNull("jwt.secret")?.getString() ?: "BOGUSSECRET"
    val realmFromConfig = environment.config.propertyOrNull("jwt.realm")?.getString() ?: "BOGUSREALM"
    val audienceFromConfig = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "BOGUSAUDIENCE"
    val issuerFromConfig = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "BOGUSISSUER"
    val ktorDeploymentPortFromConfig = (environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8787").toInt()
    val ktorApplicationModulesFromConfig = environment.config.propertyOrNull("ktor.application.modules")?.getList() ?: listOf<String>()

    println(
        """
        ${"$-=".repeat(20)}
        secretFromConfig:                      $secretFromConfig
        realmFromConfig:                       $realmFromConfig
        audienceFromConfig:                    $audienceFromConfig
        issuerFromConfig:                      $issuerFromConfig
        ktorDeploymentPortFromConfig:          $ktorDeploymentPortFromConfig
        ktorApplicationModulesFromConfig:      $ktorApplicationModulesFromConfig
        no. ktorApplicationModulesFromConfig:  ${ktorApplicationModulesFromConfig.size}
        ${"$-=".repeat(20)}
    """.trimIndent()
    )
    val secret = "ut920BwH09AOEDx5"
    val myRealm = "Access to game"
    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null

                }
            }
            challenge { defaultScheme, realm ->
                val status: Status =
                    if (call.request.uri.lowercase().contains("move")) Status.MOVE_REQUEST_WITHOUT_AUTHORIZATION
                    else Status.AUTHORIZATION_FAILED
                call.respond(status.statusCode, mapOf("status" to status.message))
            }
        }
    }


}
