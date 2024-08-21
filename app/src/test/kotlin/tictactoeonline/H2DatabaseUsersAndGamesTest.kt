package tictactoeonline

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tictactoeonline.util.MyStringTools
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class H2DatabaseUsersAndGamesTest {
    private val databaseFile = File(H2DatabaseTestMetaData.h2FileForTestingUsersAndGames)
    private val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")

    private val db by lazy {
        Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }

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

    /**
     * Game DAO
     *
     * @constructor
     *
     * @param id
     */
    class Game(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, Game>(Games)

        val player1 by Games.player1
        val player2 by Games.player2

    }


    @Test
    fun `test users and games with DAOs`() {
        transaction {

        }
    }

    @Test
    fun `test users and games with JDBC`() {
        transaction {

            val user1 = Users.insert {
                it[tictactoeonline.Users.id] = "carl@example.com"
                it[tictactoeonline.Users.password] = "1111"
                it[jwt] = MyStringTools.randomStringByKotlinRandom(23)
            }
            val user2 = Users.insert {
                it[tictactoeonline.Users.id] = "mike@example.com"
                it[tictactoeonline.Users.password] = "2222"
            }
            val game1 = Games.insert {
                it[tictactoeonline.Games.player1] = user1[Users.id]
                it[tictactoeonline.Games.player2] = user2[Users.id]

            }
            assertEquals(1, Games.selectAll().count())
            val game2 = Games.insert {
                it[tictactoeonline.Games.player1] = user1[Users.id]
                it[tictactoeonline.Games.player2] = user2[Users.id]

            }
            assertEquals(2, Games.selectAll().count())
            Games.selectAll().forEach { game ->
                val player1 = game[Games.player1]
                val player2 = game[Games.player2]
                exposedLogger.info("player1: ${player1} for game ${game[Games.id]}")
                exposedLogger.info("player2 ${player2} for game ${game[Games.id]}")
            }

        }
    }


    @BeforeEach
    fun setup() {
        if (databaseFileOnFileSystem.exists()) {
            databaseFileOnFileSystem.delete()
            assertFalse(databaseFileOnFileSystem.exists())
        }
        // just simply reference this so that it can be lazily instantiated
        db
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Users, Games)
            exposedLogger.info(
                """
            setup *****
            +${"--".repeat(40)}
            | databaseFile.absoluteFile:              ${databaseFile.absoluteFile}
            | databaseFileOnFileSystem.absoluteFile:  ${databaseFileOnFileSystem.absoluteFile}
            | databaseFileOnFileSystem.exists():      ${databaseFileOnFileSystem.exists()}
            +${"--".repeat(40)}
        """.trimIndent()
            )
        }


    }


}