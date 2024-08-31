package tictactoeonline

import com.auth0.jwt.algorithms.Algorithm
import kotlinx.serialization.Serializable
const val MAX_VARCHAR_LENGTH: Int = 128
class H2DatabaseTestMetaData {
    companion object {
        val h2FileForTesting = "./build/johnsH2Test"
        val h2FileForTestingUsersAndGames = "./build/usersAndGamesH2Test"
        val h2FileForTestingBaeldungStarWars = "./build/h2FileForTestingBaeldungStarWars"
        val h2FileForTestingBaeldungDAOStarWars = "./build/h2FileForTestingBaeldungDAOStarWars"
        val h2FileForApplicationTestWithApplicationTest = "./build/applicationTestWithApplicationTest"
        val h2InMemoryJdbcUrl = "jdbc:h2:mem:test"
        val h2FileBasedJdbcUrl = "jdbc:h2:$h2FileForTesting"
        val h2JdbcDriverClassName = "org.h2.Driver"


    }
}

/**
 * place to declare various data items used during application testing
 *
 */

@Serializable
data class MyJwtPayload(val alg: String, val typ: String)

@Serializable
data class MyJwtHeader(val alg: String, val typ: String)


const val jwtArtem =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImFydGVtQGh5cGVyc2tpbGwub3JnIn0.EYNWizxGYKbvbF1ah4EC4TTnyqbquElEzpJqE5jdwrM"
const val emailArtem = "artem@hyperskill.org"
const val jwtMike =
    """eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2VAZXhhbXBsZS5jb20ifQ.yw_YW7lY77UncwcPNOxego1l1TcP80o1gHhrdWeg0JU"""
const val emailMike = "mike@example.com"
const val jwtCarl =
    """eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImNhcmxAZXhhbXBsZS5jb20ifQ.Zt8gtkUjEffgODauxx9gwQ1cnKueML-5ESBkJW2g5AY"""
const val emailCarl = "carl@example.com"
const val secretForJwt = "ut920BwH09AOEDx5"
val algorithm = Algorithm.HMAC256(secretForJwt)
