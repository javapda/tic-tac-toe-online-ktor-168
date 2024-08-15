package tictactoeonline.util

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import tictactoeonline.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

/**
 * JWT test
 * used to test and gain better understanding of JWT
 * @constructor Create empty JWT test
 */
class JWTTest {
    @Test
    fun `test jwt withHeader and withClaim`() {
        val headerMap = mapOf("alg" to "HS256", "typ" to "JWT")
        val now = Instant.now()
        // here, we create a JWT token String
        val jwt = JWT.create()
            .withHeader(headerMap)
            .withClaim("string-claim", "string-value")
            .withClaim("number-claim", 42)
            .withClaim("bool-claim", true)
//            .withClaim("datetime-claim", now)
            .withClaim("email", emailCarl)
            .sign(algorithm);

        val dc = JWT.require(algorithm).build().verify(jwt)
        val bc = String(Base64.getDecoder().decode(dc.header))

        assertEquals("HS256", Json.decodeFromString<Map<String, String>>(bc)["alg"])
        assertEquals("JWT", Json.decodeFromString<Map<String, String>>(bc)["typ"])
        assertEquals(emailCarl, dc.getClaim("email").asString())
        assertEquals("string-value", dc.getClaim("string-claim").asString())
        assertEquals(42, dc.getClaim("number-claim").asInt())
        assertTrue(dc.getClaim("bool-claim").asBoolean())
//        assertEquals(
//            now.toString().substring(0, 19),
//            dc.getClaim("datetime-claim").asInstant().toString().substring(0, 19)
//        )
    }

    @Test
    fun `parse jwt for carl`() {
        val dc: DecodedJWT = JWT.require(algorithm).build().verify(jwtCarl)
        val emailFromJwt = dc.getClaim("email").asString()
        assertEquals(emailCarl, emailFromJwt)
    }

    @Test
    fun `parse jwt for mike`() {
        val dc: DecodedJWT = JWT.require(algorithm).build().verify(jwtMike)
        val emailFromJwt = dc.getClaim("email").asString()
        assertEquals(emailMike, emailFromJwt)
    }

    @Test
    fun `parse jwt for artem`() {
        val dc: DecodedJWT = JWT.require(algorithm).build().verify(jwtArtem)
        val emailFromJwt = dc.getClaim("email").asString()
        assertEquals(emailArtem, emailFromJwt)
    }

    /**
     * Well-known j w t from project
     * https://github.com/auth0/java-jwt
     * @param data
     */
    @ParameterizedTest
    @MethodSource("wellknownJwtFromProjectTriples")
    fun `well-known JWT from project`(data: Triple<String, String, String>) {
        val (jwt, headerJson, payloadJson) = data
        val dc: DecodedJWT = JWT.require(algorithm).build().verify(jwt)
        val headerBase64 = dc.header
        val payloadBase64 = dc.payload
        val signatureBase64 = dc.signature
        val email = dc.getClaim("email").asString()

        println(
            """
            jwt:          $jwt
            headerJson:   $headerJson
            paylaodJson:  $payloadJson
            ${"-".repeat(60)}
            headerBase64:    $headerBase64
            payloadBase64:   $payloadBase64
            signatureBase64: $signatureBase64
            email:           $email
        """.trimIndent()
        )
        assertEquals(jwt, "$headerBase64.$payloadBase64.$signatureBase64")
    }


    @Test
    fun `simple JWT token`() {
        val email = "test@email.com"
        val token = JWT.create()
            .withClaim("email", email)
            .sign(algorithm)
        println(
            """
            ${"#".repeat(80)}
            token: $token
            ${"#".repeat(80)}
        """.trimIndent()
        )
        assertEquals(3, token.split("\\.".toRegex()).size)
        val header = token.split("\\.".toRegex())[0]
        val payload = token.split("\\.".toRegex())[1]
        val decoder = Base64.getDecoder()
        println(String(decoder.decode(header)))
//        assertEquals("""{"alg":"HS256","typ":"JWT"}""", String(decoder.decode(header)))
        assertEquals("""{"email":"test@email.com"}""", String(decoder.decode(payload)))

    }

    @Test
    fun `JWT token with expiration date`() {
        val email = "test@email.com"
        val audience = "myAudience"
        val issuer = "http://javapda.com"
        val tz: ZoneId = ZoneId.systemDefault()
        val tzo: ZoneOffset = ZoneOffset.ofHours(0)
        val ldt = LocalDateTime.of(2024, 8, 9, 7, 12)
        val out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        val issuedDate = LocalDateTime.of(2024, 8, 9, 7, 12)
        val expirationDate = issuedDate.plusMinutes(10) // good for 10 minutes
        val token = JWT.create()
//            .withPayload(mapOf("email" to email))
            .withClaim("email", email)
            .withAudience(audience)
            .withIssuer(issuer)
            .withIssuedAt(issuedDate.toDate())
            .withExpiresAt(expirationDate.toDate())
            .sign(algorithm)

        println(
            """
            ${"#".repeat(80)}
            token: $token
            ${"#".repeat(80)}
        """.trimIndent()
        )
        assertEquals(3, token.split("\\.".toRegex()).size)
        val header = token.split("\\.".toRegex())[0]
        val payload = token.split("\\.".toRegex())[1]
        val decoder = Base64.getDecoder()
        val jsonHeader = String(decoder.decode(header))
        println(jsonHeader)
        val myHeader = Json.decodeFromString<MyJwtHeader>(jsonHeader)
        assertEquals("HS256", myHeader.alg)
        assertEquals("JWT", myHeader.typ)
        val jsonPayload = String(decoder.decode(payload))
        println(
            """
            jsonPayload: $jsonPayload
        """.trimIndent()
        )
        @Serializable
        data class MyPayload(val aud: String, val iss: String, val exp: Long, val iat: Long, val email: String)

        val mp = Json { ignoreUnknownKeys = true }.decodeFromString<MyPayload>(jsonPayload)
        assertEquals(audience, mp.aud)
        assertEquals(issuer, mp.iss)
        assertEquals(email, mp.email)
        assertEquals(issuedDate.toDate().time / 1000, mp.iat)
        assertEquals(expirationDate.toDate().time / 1000, mp.exp)

    }

    companion object {

        @JvmStatic
        fun wellknownJwtFromProjectTriples(): List<Triple<String, String, String>> {
            return listOf(
                Triple(
                    jwtCarl,
                    """{"alg":"HS256","typ":"JWT"}""",
                    """{"email":"carl@example.com"}"""
                )
            )
        }
    }

}