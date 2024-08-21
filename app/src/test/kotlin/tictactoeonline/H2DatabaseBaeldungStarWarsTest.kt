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

/**
 * H2database baeldung star wars test
 * https://www.baeldung.com/kotlin/exposed-persistence
 * @constructor Create empty H2database baeldung star wars test
 */
class H2DatabaseBaeldungStarWarsTest {
    private val databaseFile = File(H2DatabaseTestMetaData.h2FileForTestingBaeldungStarWars)
    private val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")

    private val db by lazy {
        Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
    }

    /**
     * Star wars films_simple
     * https://www.baeldung.com/kotlin/exposed-persistence#1-columns
     * @constructor Create empty Star wars films_simple
     */
    object StarWarsFilms_Simple : Table() {
        val id = integer("id").autoIncrement()
        val sequelId = integer("sequel_id").uniqueIndex()
        val name = varchar("name", 50)
        val director = varchar("director", 50)
        override val primaryKey = PrimaryKey(id, name = "PK_StarWarsFilms_Id")
    }

    /**
     * Star wars films
     * https://www.baeldung.com/kotlin/exposed-persistence#2-primary-keys
     * @constructor Create empty Star wars films
     */
    object StarWarsFilms : IntIdTable("STAR_WARS_FILMS") {
        val sequelId = integer("sequel_id").uniqueIndex()
        val name = varchar("name", 50)
        val director = varchar("director", 50)
    }

    /**
     * Players
     * https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
     * @constructor Create empty Players
     */
    object Players : Table() {
        val sequelId = integer("sequel_id")
            .uniqueIndex()
            .references(StarWarsFilms.sequelId)

        // the following is effectively the same as the previous statement
        //val sequelId = reference("sequel_id", StarWarsFilms.sequelId).uniqueIndex()
        val name = varchar("name", 50)

        // from https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
        // here, since our filmId is reference to primary key in StarWarsFilms, no .<fieldName> required
        val filmId = reference("film_id", StarWarsFilms)
    }

    @Test
    fun `inserting data 7_1`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#1-inserting-data
        transaction {
            StarWarsFilms.insert {
                it[name] = "The Last Jedi"
                it[sequelId] = 8
                it[director] = "Rian Johnson"
            }
            assertEquals(1, StarWarsFilms.select(StarWarsFilms.columns).count())
        }

    }

    @Test
    fun `Extracting Auto-Increment Column Values`() {
        transaction {
            val id = StarWarsFilms.insertAndGetId {
                it[name] = "The Last Jedi"
                it[sequelId] = 8
                it[director] = "Rian Johnson"
            }
            assertEquals(1, id.value)

            val insert = StarWarsFilms.insert {
                it[name] = "The Force Awakens"
                it[sequelId] = 7
                it[director] = "J.J. Abrams"
            }
            assertEquals(2, insert[StarWarsFilms.id].value)
            assertEquals(2, StarWarsFilms.select(StarWarsFilms.id).count())
        }
    }

    @Test
    fun `Updating Data 7_3`() {
        transaction {
            val id = StarWarsFilms.insertAndGetId {
                it[name] = "The Last Jedi"
                it[sequelId] = 8
                it[director] = "Rian Johnson"
            }
            assertEquals(1, id.value)
            val numUpdated = StarWarsFilms.update({ StarWarsFilms.sequelId eq 8 }) {
                it[name] = "Episode VIII - The Last Jedi"
            }
            assertEquals(1, numUpdated)
        }

        transaction {
            // When we need to update a column by computing a new value
            // from the old value, we leverage the SqlExpressionBuilder
            val where: SqlExpressionBuilder.() -> Op<Boolean> = { StarWarsFilms.sequelId eq 8 }
            StarWarsFilms.update(where) {
                with(SqlExpressionBuilder) {
                    it.update(StarWarsFilms.sequelId, StarWarsFilms.sequelId + 1)
                }
            }
        }
    }

    @Test
    fun `Deleting Data`() {
        transaction {
            transaction {
                val id = StarWarsFilms.insertAndGetId {
                    it[name] = "The Last Jedi"
                    it[sequelId] = 8
                    it[director] = "Rian Johnson"
                }
                assertEquals(1, id.value)

                val insert = StarWarsFilms.insert {
                    it[name] = "The Force Awakens"
                    it[sequelId] = 7
                    it[director] = "J.J. Abrams"
                }
                assertEquals(2, insert[StarWarsFilms.id].value)
                val numDeleted = StarWarsFilms.deleteWhere { StarWarsFilms.sequelId eq 8 }
                assertEquals(1, numDeleted)
            }
        }
    }

    @Test
    fun `test logging`() {
        exposedLogger.trace("trace")
        exposedLogger.info("info")
        exposedLogger.warn("warn")
        exposedLogger.error("error")
        exposedLogger.debug("debug")
    }

    @Test
    fun `test commit`() {
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#1-commit-and-rollback
            exposedLogger.info("committing")
            commit()
            exposedLogger.info("committed")
        }
    }

    @Test
    fun `test star wars - queries`() {
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#1-select-all
            val query = StarWarsFilms.selectAll()
            query.forEach {
                assertTrue { it[StarWarsFilms.sequelId] >= 7 }
            }
            StarWarsFilms.select(StarWarsFilms.columns).forEach {
                assertTrue { it[StarWarsFilms.sequelId] >= 7 }
            }
        }
    }

    @Test
    fun `Advanced Filtering 6_4`() {
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#4-advanced-filtering
            StarWarsFilms.insert {
                // https://www.baeldung.com/kotlin/exposed-persistence#auto-increment
                it[name] = "The Force Awakens"
                it[sequelId] = 7
                it[director] = "J.J. Abrams"
            }
            StarWarsFilms.insert {
                // https://www.baeldung.com/kotlin/exposed-persistence#auto-increment
                it[name] = "Monkey Not Star Wars"
                it[sequelId] = 37
                it[director] = "J.J. Abrams"
            }

            val query = StarWarsFilms.selectAll()
            query.withDistinct(true).forEach { film ->
                exposedLogger.info("FILM====>${film}")
            }

            query.limit(20, offset = 40).forEach { film ->
                exposedLogger.info("FILM====>${film}")
            }
        }
    }

    @Test
    fun `joins innerJoin`() {
        transaction {
            val film1 = StarWarsFilms.insert { film ->
                film[director] = "JED"
                film[sequelId] = 19
                film[name] = "Monkey and the Gorilla Guy"

            }
//            Players.insert { player ->
//                player[Players.filmId] = film1
//                player[Players.name] = "I am a player of some sort"
//
//            }
        }
        transaction {
            // https://www.baeldung.com/kotlin/exposed-persistence#6-joins
            (StarWarsFilms innerJoin Players).selectAll().forEach { film ->
                exposedLogger.info("FILM====>${film}, ${film[StarWarsFilms.director]}")
            }
            (StarWarsFilms innerJoin Players)
                .select { StarWarsFilms.sequelId eq Players.sequelId }.forEach { film ->
                    exposedLogger.info("FILM====>${film}, ${film[StarWarsFilms.director]}")
                }

            val complexJoin = Join(
                StarWarsFilms,
                Players,
                onColumn = StarWarsFilms.sequelId, otherColumn = Players.sequelId,
                joinType = JoinType.INNER,
                additionalConstraint = { StarWarsFilms.sequelId eq 8 }
            )
            complexJoin.selectAll().forEach { resultRow ->
                exposedLogger.info("resultRow: ${resultRow}")
            }

        }
    }

    @Test
    fun `Aliasing 6_7`() {
        transaction {
            (StarWarsFilms innerJoin Players)
                .selectAll()
                .forEach {
                    assertEquals(it[StarWarsFilms.sequelId], it[Players.sequelId])
                }
            // when same table appears more than once in a query, we might want
            // to give it an alias:
            val sequel = StarWarsFilms.alias("sequel")

            // now we can use the alias :
            Join(StarWarsFilms, sequel,
                additionalConstraint = {
                    sequel[StarWarsFilms.sequelId] eq StarWarsFilms.sequelId + 1
                }).selectAll().forEach {
                assertEquals(
                    it[sequel[StarWarsFilms.sequelId]], it[StarWarsFilms.sequelId] + 1
                )
            }
            // above, sequel is an alias for a table participating in a join.
            // when we want to access one of its column, we use the aliased table's column as a key:
            // sequel[StarWarsFilms.sequelId]
        }

    }

    @Test
    fun `Order By and Group By 6_5`() {
        transaction {
            val query = StarWarsFilms.selectAll()
            query.orderBy(StarWarsFilms.name to SortOrder.ASC)

            StarWarsFilms.select(StarWarsFilms.sequelId.count(), StarWarsFilms.director).groupBy(StarWarsFilms.director)
                .forEach { film ->
                    exposedLogger.info("FILM====>${film}")
                }
        }
    }

    @Test
    fun `filtering with where expressions`() {
        transaction {
            StarWarsFilms.insert {
                // https://www.baeldung.com/kotlin/exposed-persistence#auto-increment
                it[name] = "The Force Awakens"
                it[sequelId] = 7
                it[director] = "J.J. Abrams"
            }
            val select =
                StarWarsFilms.select { (StarWarsFilms.director like "J.J.%") and (StarWarsFilms.sequelId eq 7) }
            assertEquals(1, select.count())

            val sequelNo = 7
            StarWarsFilms.select { StarWarsFilms.sequelId greaterEq sequelNo }
            // below is same as above - just not deprecated
            StarWarsFilms.selectAll().where { StarWarsFilms.sequelId greaterEq sequelNo }
        }
    }

    @Test
    fun `selecting a subset of columns`() {
        // https://www.baeldung.com/kotlin/exposed-persistence#2-selecting-a-subset-of-columns
        transaction {

            StarWarsFilms.slice(StarWarsFilms.name, StarWarsFilms.director).selectAll().forEach {
                assertTrue { it[StarWarsFilms.name].startsWith("The") }
            }
            StarWarsFilms.select(StarWarsFilms.name, StarWarsFilms.director).forEach {
                assertTrue { it[StarWarsFilms.name].startsWith("The") }
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
            // https://www.baeldung.com/kotlin/exposed-persistence#2-logging-statements
            addLogger(StdOutSqlLogger)

            // https://www.baeldung.com/kotlin/exposed-persistence#4-creating-tables
            SchemaUtils.create(StarWarsFilms, StarWarsFilms_Simple, Players)
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