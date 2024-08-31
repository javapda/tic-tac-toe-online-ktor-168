package tictactoeonline.exposed

import io.ktor.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Many to one test
 * Here we work through an example of a many-to-one relationshipo
 * based on:
 *   https://www.baeldung.com/kotlin/exposed-persistence
 *   https://github.com/Baeldung/kotlin-tutorials/tree/master/kotlin-libraries
 *
 * The many-to-one
 *   Many :
 *
 */
class ManyToOneTest {

    // Tables
    object MTORockyFilms : IntIdTable("mto_rocky_films") {
        val sequelId = integer("sequel_id").uniqueIndex()
        val name = varchar("name", 50)
        val director = varchar("director", 50)
    }

    object MTOPlayers : Table("mto_players") {
        val sequelId = reference("sequel_id", MTORockyFilms.sequelId) //.uniqueIndex()
        val name = varchar("name", 50)
        val filmId = reference("film_id", MTORockyFilms)
        override val primaryKey = PrimaryKey(
            sequelId, name,
            name = "PK_MTOPlayers_sequelId_name"
        )
    }

    object MTOUsers : IntIdTable("mto_users") {
        val name = varchar("name", 50)
    }

    object MTOUserRatings : IntIdTable("mto_user_ratings") {
        val value = long("value")
        val film = reference("film", MTORockyFilms)
        val user = reference("user", MTOUsers)
    }

    @Test
    fun `h2 many-to-one testing`() = `exercise many-to-one`(Application::h2ManyToOne)

    @Test
    fun `postgres many-to-one testing`() = `exercise many-to-one`(Application::postgresManyToOne)

    private fun `exercise many-to-one`(moduleFunction: Application.() -> Unit) {
        fun showSomeSql() {
            transaction {
                val predicates = Op.build {
                    (MTORockyFilms.sequelId inList listOf(1, 2, 3))
                }
                val plainSQLSelectAll = MTORockyFilms.selectAll() //.where {}.prepareSQL(QueryBuilder(false))
//                    .andWhere { predicates }
                    .where { MTORockyFilms.sequelId inList listOf(1, 2, 3) }
                    .prepareSQL(QueryBuilder(false))
                val plainSQLSelectSome =
                    MTORockyFilms.select(MTORockyFilms.name) //.where {}.prepareSQL(QueryBuilder(false))
//                    .andWhere { predicates }
                        .where { MTORockyFilms.sequelId inList listOf(1, 2, 3) }
                        .orderBy(MTORockyFilms.sequelId, SortOrder.DESC)
                        .prepareSQL(QueryBuilder(false))
                println(
                    """
                    ${"-".repeat(60)}
                    sql-all:   $plainSQLSelectAll
                    sql-some:  $plainSQLSelectSome
                    ${"-".repeat(60)}
                """.trimIndent()
                )
            }
        }
        withTestApplication(moduleFunction) {
            transaction {
                val tables = arrayOf(MTOUsers, MTOUserRatings, MTOUsers, MTOPlayers, MTORockyFilms)
                SchemaUtils.drop(*tables)
                SchemaUtils.create(*tables)
            }
            fun addRockyFilm(theName: String, theSeqlId: Int, theDirector: String): Int {
                return MTORockyFilms.insertAndGetId {
                    it[name] = theName
                    it[sequelId] = theSeqlId
                    it[director] = theDirector
                }.value

            }

            fun addRockyFilmPlayer(rockyFilmId: Int, playerName: String) {
                val sid = MTORockyFilms.select(MTORockyFilms.sequelId).where { MTORockyFilms.id eq rockyFilmId }
                    .firstOrNull()!![MTORockyFilms.sequelId]
                println("addRockyFilmPlayer: rockyFilmId=$rockyFilmId, playerName=$playerName, sequelId=$sid")
                if (sid > 5) {
                    println("sid > 5: $sid")
                }
                MTOPlayers.insert {
                    it[name] = playerName
                    it[sequelId] = sid
//                        MTORockyFilms.select(MTORockyFilms.sequelId).where { MTORockyFilms.id eq rockyFilmId }
//                            .firstOrNull()!![MTORockyFilms.sequelId]
                    it[filmId] = rockyFilmId
                }
            }
            // populate tables
            transaction {
                val rocky = addRockyFilm("Rocky", 0, "John G. Avildsen")
                val rockyII = addRockyFilm("Rocky II", 1, "Sylvester Stallone")
                val rockyIII = addRockyFilm("Rocky III", 2, "Sylvester Stallone")
                val rockyIV = addRockyFilm("Rocky IV", 3, "Sylvester Stallone")
                val rockyV = addRockyFilm("Rocky V", 4, "John G. Avildsen")
                println(
                    """
                    ROCKY FILMS ID
                    rocky:       $rocky
                    rockyII:     $rockyII
                    rockyIII:    $rockyIII
                    rockyIV:     $rockyIV
                    rockyV:      $rockyV
                """.trimIndent()
                )
                assertEquals(5, MTORockyFilms.selectAll().count())
                assertEquals(
                    2,
                    MTORockyFilms.selectAll()
                        .where { MTORockyFilms.director.upperCase() like "John G. Avildsen".uppercase() }.count()
                )
                assertEquals(
                    3,
                    MTORockyFilms.selectAll()
                        .where { MTORockyFilms.director.upperCase() like "%stallon%".uppercase() }.count()
                )
                // distinct directors
                assertEquals(2, MTORockyFilms.select(MTORockyFilms.director).withDistinct(true).count())
                assertEquals(2, MTORockyFilms.selectAll().limit(2).count())
                assertEquals(5, MTORockyFilms.selectAll().limit(200).count())
                assertEquals(MTORockyFilms.selectAll().count(), MTORockyFilms.selectAll().limit(200).count())
                MTORockyFilms.selectAll().orderBy(MTORockyFilms.director, SortOrder.DESC).forEach { row ->
                    val director = row[MTORockyFilms.director]
                    val name = row[MTORockyFilms.name]
                    println("$director")
                }
                val rockySequelId = MTORockyFilms.select(MTORockyFilms.sequelId).where { MTORockyFilms.id eq rockyIII }
                    .firstOrNull()!![MTORockyFilms.sequelId]
                println("rockySequelId:  $rockySequelId")
                addRockyFilmPlayer(rocky, "Sylvester Stallone")
                addRockyFilmPlayer(rocky, "Talia Shire")
                addRockyFilmPlayer(rocky, "Burt Young")
                addRockyFilmPlayer(rocky, "Carl Weathers")
                addRockyFilmPlayer(rocky, "Burgess Meredith")
                addRockyFilmPlayer(rockyII, "Sylvester Stallone")
                println("rockyIII=$rockyIII")
                addRockyFilmPlayer(rockyIII, "Sylvester Stallone")
                (MTORockyFilms innerJoin MTOPlayers).selectAll().forEach { row ->
                    println("ZZZ row:name=${row[MTORockyFilms.name]}: $row ")
                }
                // report the number of films for each director
                MTORockyFilms
                    .select(MTORockyFilms.director, MTORockyFilms.sequelId.count().alias("NUMBER_OF_FILMS"))
                    .groupBy(MTORockyFilms.director)
                    .forEach { row ->
                        println("${row[MTORockyFilms.sequelId.count()]} :  ${row[MTORockyFilms.director]}")
                        println("YYY row: $row ")
                    }

            }
            showSomeSql()
        }

    }

}

fun Application.h2ManyToOne() {
    // setup everything
    fun h2Database(clearDatabase: Boolean = false): Database {
        val databaseFile =
            File("./build/h2ManyToOne") // results in PROJECT/app/build/db.mv.db
        val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")
        if (clearDatabase) {
            if (databaseFileOnFileSystem.exists()) {
                databaseFileOnFileSystem.delete()
            }
        }
        return Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }
    h2Database(true)
}

fun Application.postgresManyToOne() {
    fun postgresDatabase(): Database {
        return Database.connect(
            "jdbc:postgresql://localhost:5432/gamedb",
            user = "gamedb",
            password = "gamedb"
        )
    }
    postgresDatabase()

}
