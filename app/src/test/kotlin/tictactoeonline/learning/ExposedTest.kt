package tictactoeonline.learning

import org.h2.util.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test



class ExposedTest {
    val h2FileForTesting="johnsTest"
    val h2InMemoryJdbcUrl = "jdbc:h2:mem:test"
    val h2FileBasedJdbcUrl = "jdbc:h2:$h2FileForTesting"
    val h2JdbcDriverClassName = "org.h2.Driver"

    @Test
    fun `learning Exposed`() {
        val db1 = Database.connect(h2FileBasedJdbcUrl, driver = h2JdbcDriverClassName)
        println(db1)
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }
}