package tictactoeonline

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
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

        // one-to-many : one StarWarsFilm may have zero-or-many UserRating
        // NOTE: this must be immutable (val) - can only be read
        val ratings by UserRating referrersOn UserRatings.film

        var actors by Actor via StarWarsFilmActors
    }

    object Actors : IntIdTable() {
        val firstName = varchar("firstname", 50)
        val lastname = varchar("lastname", 50)
    }

    class Actor(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Actor>(Actors)

        var firstname by Actors.firstName
        var lastname by Actors.lastname
    }

    // this is the link table between StarWarsFilm and Actors
    object StarWarsFilmActors : Table() {
        // table (link-table) to tie StarWarsFilm to Actors
        // table has 2 columns, both are foreign keys making up a composite primary key
        val starWarsFilm = reference("starWarsFilm", StarWarsFilms)
        val actor = reference("actor", Actors)
        override val primaryKey = PrimaryKey(
            starWarsFilm, actor, name = "PK_StarWarsFilmActors_swf_act"
        )
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

    object Users : IntIdTable() {
        val name = varchar("name", 50)
    }

    class User(id: EntityID<Int>) : IntEntity(id) {
        // tie this DAO to the underlying table
        companion object : IntEntityClass<User>(Users)

        var name by Users.name
    }

    object UserRatings : IntIdTable() {
        val value = long("value")
        val film = reference("film", StarWarsFilms)
        val user = reference("user", Users).nullable()
    }

    class UserRating(id: EntityID<Int>) : IntEntity(id) {
        // tie this DAO to the underlying table
        companion object : IntEntityClass<UserRating>(UserRatings)

        // link/tie local properties to their table equivalents
        var value by UserRatings.value
        var film by StarWarsFilm referencedOn UserRatings.film

        // using optionalReferencedOn allows us to put a null in the field (but the field must first be .nullable())
        // this is SO COOL, since you are prevented from inadvertently making something null that is not supposed to be
        var user by User optionalReferencedOn UserRatings.user
//        var user by User referencedOn UserRatings.user
    }

    @Test
    fun `Many-to-One Associations 8_5`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#5-many-to-one-associations
        // 2-step proces
        // step 1: create the Table objects (Users,UserRatings,etc.)
        // step 2: create the DAO classes (UserRating)
        transaction {
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                director = "Rian Johnson"
                sequelId = 8
            }
            val someUser = User.new {
                name = "Some User"
            }
            val rating = UserRating.new {
                value = 9
                user = someUser
                film = theLastJedi
            }
            assertEquals(theLastJedi, rating.film)
        }
    }

    @Test
    fun `Optional Associations 8_6`() {
        transaction {
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                director = "Rian Johnson"
                sequelId = 8
            }
            val someUser = User.new {
                name = "Some User"
            }
            val rating = UserRating.new {
                value = 9
//                user = someUser
                film = theLastJedi
            }
            assertEquals(theLastJedi, rating.film)
            assertNull(rating.user)
        }

    }

    @Test
    fun `One-to-Many Associations 8_7`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#7-one-to-many-associations
        transaction {
            val theLastJedi = StarWarsFilm.new {
                name = "The Last Jedi"
                director = "Rian Johnson"
                sequelId = 8
            }
            val someUser1 = User.new {
                name = "Some User : 1"
            }
            val someUser2 = User.new {
                name = "Some User : 2"
            }
            val rating1 = UserRating.new {
                value = 9
//                user = someUser1
                film = theLastJedi
            }
            val rating2 = UserRating.new {
                value = 3
                user = someUser2
                film = theLastJedi
            }
            assertEquals(theLastJedi, rating1.film)
            assertNull(rating1.user)
            assertEquals(2, theLastJedi.ratings.count())

        }
    }

    @Test
    fun `Many-to-Many Associations 8_8`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#8-many-to-many-associations
        var film: StarWarsFilm? = null
        var actor: Actor? = null
        film = transaction {
            StarWarsFilm.new {
                name = "The Last Jedi"
                sequelId = 8
                director = "Rian Johnson"
            }
        }

        actor = transaction {
            Actor.new {
                firstname = "Daisy"
                lastname = "Ridley"
            }
//            assertEquals(1, Actors.selectAll().count())
//            exposedLogger.info("That is many-to-many associations")
        }
        transaction {
            // link the actors to the container
            film.actors = SizedCollection(listOf(actor))

        }
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
        delay(delaySeconds * 1000)
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
                Actors,
                StarWarsFilms,
                StarWarsFilmActors,
                Users,
                UserRatings
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