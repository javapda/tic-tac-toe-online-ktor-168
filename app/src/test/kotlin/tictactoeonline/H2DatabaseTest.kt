package tictactoeonline

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


object Users : Table(name = "users") {
    val id = varchar("email", MAX_VARCHAR_LENGTH)
    val password = varchar("password", MAX_VARCHAR_LENGTH)
    var jwt = varchar("jwt", 3 * MAX_VARCHAR_LENGTH).nullable().default(null)
    override val primaryKey = PrimaryKey(id, name = "PK_User_Email")
}

object Games : IntIdTable(name = "games") {
    val player1 = reference("player1", Users.id, onDelete = ReferenceOption.SET_NULL)
    val player2 = reference("player2", Users.id, onDelete = ReferenceOption.SET_NULL)
}

object Tasks : Table(name = "tasks") {
    // https://jetbrains.github.io/Exposed/getting-started-with-exposed.html#define-table-object
    val id = integer("id").autoIncrement()
    val title = varchar("name", MAX_VARCHAR_LENGTH)
    val description = varchar("description", MAX_VARCHAR_LENGTH)
    val isCompleted = bool("completed").default(false)
}


class H2DatabaseTest {

    val databaseFile = File(H2DatabaseTestMetaData.h2FileForTesting)
    val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")

    //lateinit var db: Database
    val db by lazy {
//        Database.connect(/* setup connection */)
        Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }

    @BeforeEach
    fun setup() {
        println(
            """
            setup *****
            databaseFile.absoluteFile:              ${databaseFile.absoluteFile}
            databaseFileOnFileSystem.absoluteFile:  ${databaseFileOnFileSystem.absoluteFile}
            databaseFileOnFileSystem.exists():      ${databaseFileOnFileSystem.exists()}
        """.trimIndent()
        )
        if (databaseFileOnFileSystem.exists()) {
            databaseFileOnFileSystem.delete()
            assertFalse(databaseFileOnFileSystem.exists())
        }
        //db = Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
        // just simply reference this so that it can be lazily instantiated
        db

    }

    @Test
    fun `test the H2 database file`() {

        transaction() {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Tasks)

            // create some data
            val taskId = Tasks.insert {
                it[title] = "Learn Exposed"
                it[description] = "Go through the Get started with Exposed tutorial"
            } get Tasks.id
            assertEquals(1, taskId)

            val secondTaskId = Tasks.insert {
                it[title] = "Read the hobbit"
                it[description] = "Read the first two chapters of The Hobbit"
                it[isCompleted] = true
            } get Tasks.id
            assertEquals(2, secondTaskId)

            val thirdTaskId = Tasks.insert {
                it[title] = "Atlas Shrugged"
                it[description] = "Read the first two chapters of Atlas Shrugged"
                it[isCompleted] = true
            } get Tasks.id
            assertEquals(3, thirdTaskId)

            val found = Tasks.select(Tasks.id.count().alias("jed")).first()
            println(
                """
                found:  ${found}
            """.trimIndent()
            )
        }
        assertTrue(databaseFileOnFileSystem.exists())
        transaction {
            Tasks.select(Tasks.id.count(), Tasks.isCompleted).groupBy(Tasks.isCompleted).forEach {
                println("${it[Tasks.isCompleted]}: ${it[Tasks.id.count()]} ")
            }

        }
        transaction {
            Tasks.selectAll().forEach { task ->
                println("id=${task[Tasks.id]}, title=${task[Tasks.title]}, isCompleted=${task[Tasks.isCompleted]}")
            }
        }

        transaction {
            Tasks.update({ Tasks.id eq 2 }) {
                it[isCompleted] = false

            }
            Tasks.deleteWhere { id eq 1 }

        }
        transaction {
            Tasks.selectAll().forEach { task ->
                exposedLogger.info("id=${task[Tasks.id]}, title=${task[Tasks.title]}, isCompleted=${task[Tasks.isCompleted]}")
            }
            println("Remaining tasks: ${Tasks.selectAll().toList()}")
            exposedLogger.info("HOW ARE YOU TODAY?")
        }

    }
}