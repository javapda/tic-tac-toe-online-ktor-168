package tictactoeonline

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.*

/**
 * Using Exposed's DAO API - a lightweight ORM
 *
 */
class H2DatabaseBaeldungDAOStarWarsTest {
    private val databaseFile = File(H2DatabaseTestMetaData.h2FileForTestingBaeldungDAOStarWars)
    private val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")

    private val db by lazy {
        Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }

    object StarWarsFilms : IntIdTable("STAR_WARS_FILMS") {
        val sequelId = integer("sequel_id").uniqueIndex()
        val name = varchar("name", 50)
        val director = varchar("director", 50)
    }

    class StarWarsFilm(id: EntityID<Int>) : Entity<Int>(id) {
        companion object : EntityClass<Int, StarWarsFilm>(StarWarsFilms)

        var sequelId by StarWarsFilms.sequelId
        var name by StarWarsFilms.name
        var director by StarWarsFilms.director
    }

    @Test
    fun `DAO Inserting Data 8_2`() {
        transaction {
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                sequelId = 8
                director = "Rian Johnson"
            }
            exposedLogger.info("[A1] After new/insertion statement : no actual insert yet")
            assertEquals("Rian Johnson", theLastJedi.director)
            exposedLogger.info("[A2] After new/insertion statement : still no actual insert")
            assertEquals(1, theLastJedi.id.value)
            exposedLogger.info("[A3] After new/insertion statement : INSERT just performed due to .id reference")
            assertEquals(8, theLastJedi.sequelId)
            commit()
        }

    }

    @Test
    fun `Updating and Deleting Objects 8_3`() {
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#3-updating-and-deleting-objects
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                sequelId = 8
                director = "Rian Johnson"
            }
            commit()
            theLastJedi.name = "Episode VIII - The Last Jedi"
            theLastJedi.director = "Rian Craig Johnson"
            commit()
            val retrievedTheLastJedi = StarWarsFilm.findById(1)
            assertEquals("Episode VIII - The Last Jedi", retrievedTheLastJedi!!.name)
            assertEquals("Rian Craig Johnson", retrievedTheLastJedi.director)
            retrievedTheLastJedi.delete()
            val afterDeletionRetrievedTheLastJedi = StarWarsFilm.findById(1)
            assertNull(afterDeletionRetrievedTheLastJedi)
        }
    }

    @Test
    fun `Querying 8_4`() {
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#4-querying
            val movies = StarWarsFilm.all()
            assertTrue(movies.empty())
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                sequelId = 8
                director = "Rian Johnson"
            }
            commit()
            val retrievedMovies = StarWarsFilm.all()
            assertEquals(1, retrievedMovies.count())
            val singleMovie = StarWarsFilm.findById(1)
            assertNotNull(singleMovie)
            val moviesWithSequelIdOf8 = StarWarsFilm.find { StarWarsFilms.sequelId eq 8 }
            assertFalse(moviesWithSequelIdOf8.empty())
            assertEquals(1, moviesWithSequelIdOf8.count())

        }
    }

    @Test
    fun `Many-to-One Associations 8_5`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#5-many-to-one-associations
//        TODO("8.5 Many-to-One Associations")
        val names = listOf("Denise","Walker","Griffin")
        println(names.sorted())
    }

    @Test
    fun `testing coroutines`() {
        runBlocking {

            launch {
                delay(400)
                doWorld()

            }

        }
    }

    private suspend fun doWorld() {
        val delaySeconds = 5L
        println("Start doWorld, delay $delaySeconds seconds")
        delay(delaySeconds*1000)
        println("do world time")
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
            // https://www.baeldung.com/kotlin/exposed-persistence#2-logging-statements
            addLogger(StdOutSqlLogger)

            // https://www.baeldung.com/kotlin/exposed-persistence#4-creating-tables
            SchemaUtils.create(
                StarWarsFilms
            )
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