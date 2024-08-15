package tictactoeonline

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationNonRoutingTest {
    @Test
    fun `test with test application configuration`() = withTestApplication(Application::module) {

        println(
            """
            environment.application.developmentMode:  ${environment.application.developmentMode}
            environment == environment.application.environment: ${environment == environment.application.environment}
            environment.config:  ${environment.config}
            """.trimIndent()
        )

    }

    @Test
    fun `test jwt`() {
        assertEquals(emailCarl, emailFromJwt(jwtCarl))
        assertEquals(emailMike, emailFromJwt(jwtMike))
        assertEquals(emailArtem, emailFromJwt(jwtArtem))
    }


}