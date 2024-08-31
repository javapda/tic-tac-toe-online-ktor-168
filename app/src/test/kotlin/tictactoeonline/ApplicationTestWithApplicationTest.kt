package tictactoeonline

import io.ktor.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * https://medium.com/@patricktaddei/ktor-and-exposed-the-new-golden-stack-in-the-kotlin-world-b2752e5374fb
 * https://github.com/pattad/ktor-exposed
 *
 */
fun Application.dbTestModule() {
    configureTestDatabase(true)
}

// table
object MovieTable : UUIDTable("movie") {
    val imdbId = varchar("imdb_id", 50).uniqueIndex()
    val name = varchar("name", 50)
    val director = varchar("director", 50)
}

// dao
class Movies(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, Movies>(MovieTable)

    var imdbId by MovieTable.imdbId
    var name by MovieTable.name
    var director by MovieTable.director

    override fun toString(): String {
        return "Movie(imdbId=$imdbId, name='$name', directory='$director', id=$id)"
    }
}

// dto
data class Movie(val id: UUID, val imdbId: String, val name: String, val director: String)

class MovieService {
    fun getAll(): List<Movie> = transaction {
        Movies.all().map { toMovieDTO(it) }
    }

    private fun toMovieDTO(dao: Movies) = Movie(dao.id.value, dao.imdbId, dao.name, dao.director)
}

fun configureTestDatabase(clearDatabase: Boolean = false) {
    var db: Database?
    fun h2Database() : Database {
        val databaseFile =
            File(H2DatabaseTestMetaData.h2FileForApplicationTestWithApplicationTest) // results in PROJECT/app/build/db.mv.db
        val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")
        if (clearDatabase) {
            if (databaseFileOnFileSystem.exists()) {
                databaseFileOnFileSystem.delete()
            }
        }
        return Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }
    fun postgresDatabase() : Database {
        return Database.connect(
            "jdbc:postgresql://localhost:5432/gamedb",
            user = "gamedb",
            password = "gamedb"
        )
    }
//    val databaseFile =
//        File(H2DatabaseTestMetaData.h2FileForApplicationTestWithApplicationTest) // results in PROJECT/app/build/db.mv.db
//    val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")
//    if (clearDatabase) {
//        if (databaseFileOnFileSystem.exists()) {
//            databaseFileOnFileSystem.delete()
//        }
//    }
//    db = h2Database()
    db = postgresDatabase()
//    db = Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    //val db = Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    var id: UUID? = null
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.drop(MovieTable)
        SchemaUtils.create(MovieTable)
        val movie = Movies.new {
            name = "Oceans Eleven"
            imdbId = "tt0054135"
            director = "Lewis Milestone"
        }
        id = movie.id.value
        println(movie)

    }
    println("id: ${id ?: "id never set"}")
}

/**
 * Application test with application test
 *
 * yes, the name is confusing but here we want to be able to
 * do our own internal database configuration, without, necessarily
 * bootstrapping the entire production application
 */
class ApplicationTestWithApplicationTest {

    @Test
    fun `test it out`() {
        withTestApplication(Application::dbTestModule) {
            assertTrue(true)
        }
    }
}