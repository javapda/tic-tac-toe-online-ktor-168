package tictactoeonline.exposed

import io.ktor.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.test.Test

/**
 * Many to many test
 * Here we work through an example of a many-to-many relationshipo
 * based on:
 *  https://stackoverflow.com/questions/70734941/exposed-orm-dsl-vs-dao-in-many-to-many-relationships-best-practices/70735689#70735689
 * @constructor Create empty Many to many test
 */
fun Application.h2ManyToMany() {
    // setup everything
    fun h2Database(clearDatabase: Boolean = false): Database {
        val databaseFile =
            File("./build/h2ManyToMany") // results in PROJECT/app/build/db.mv.db
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

fun Application.postgresManyToMany() {
    fun postgresDatabase(): Database {
        return Database.connect(
            "jdbc:postgresql://localhost:5432/gamedb",
            user = "gamedb",
            password = "gamedb"
        )
    }
    postgresDatabase()

}

class ManyToManyTest {
    object MyUsersTable : IntIdTable("MY_USERS") {
        val username = varchar("username", 50)
    }

    class MyUser(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<MyUser>(MyUsersTable)

        var username by MyUsersTable.username
    }

    object MyPermissionsTable : IntIdTable("MY_PERMISSIONS") {
        val name = varchar("name", 50)
    }

    object MyUserPermissionsLinkTable : IntIdTable("MY_USER_PERMISSIONS_LINK") {
        val user = reference("user", MyUsersTable)
        val permission = reference("permission", MyPermissionsTable)
    }

    @Test
    fun `h2 many-to-many`() = `exercise many-to-many`(Application::postgresManyToMany)

    @Test
    fun `postgres many-to-many`() = `exercise many-to-many`(Application::postgresManyToMany)

    private fun `exercise many-to-many`(moduleFunction: Application.() -> Unit) {
        withTestApplication(moduleFunction) {
            fun setup() {
                transaction {
                    // set things up
                    addLogger(StdOutSqlLogger)
                    val tables = arrayOf(MyUsersTable, MyPermissionsTable, MyUserPermissionsLinkTable)
                    SchemaUtils.drop(*tables)
                    SchemaUtils.create(*tables)
                }
            }

            fun showSomeSql() {
                transaction {

                    val predicates = Op.build {
                        (MyUsersTable.username inList listOf("Jed", "Bambi")) and
                                (MyUsersTable.id greaterEq 0)
                    }
                    val plainSQLSelectAll = MyUsersTable.selectAll() //.where {}.prepareSQL(QueryBuilder(false))
                        .andWhere { predicates }
//                    .where{ MyUsersTable.username inList listOf("Jed", "Bambi") }
                        .prepareSQL(QueryBuilder(false))
                    val dropTableSQL = MyUsersTable.deleteAll()
                    println(
                        """
                    ${"_".repeat(50)}
                    plainSQLSelectAll: $plainSQLSelectAll
                    ${"_".repeat(50)}
                """.trimIndent()
                    )
                }

            }

            fun populateTables() {
                transaction {
                    MyUser.new {
                        username = "Wilma.Flintstone"
                    }
                    val userId = MyUsersTable.insertAndGetId {
                        it[username] = "john.smith"
                    }

                    val readPermissionId = MyPermissionsTable.insertAndGetId {
                        it[name] = "read"
                    }

                    val writePermissionId = MyPermissionsTable.insertAndGetId {
                        it[name] = "write"
                    }

                    MyUserPermissionsLinkTable.insert {
                        it[user] = userId
                        it[permission] = readPermissionId
                    }

                    MyUserPermissionsLinkTable.insert {
                        it[user] = userId
                        it[permission] = writePermissionId
                    }
                }
            }

            fun runQueries() {
                transaction {
                    val result = MyUsersTable
                        .join(MyUserPermissionsLinkTable, JoinType.INNER, additionalConstraint = {
                            MyUsersTable.id eq MyUserPermissionsLinkTable.user
                        })
                        .join(MyPermissionsTable, JoinType.INNER, additionalConstraint = {
                            MyUserPermissionsLinkTable.permission eq MyPermissionsTable.id
                        })
                        .select(MyUsersTable.username, MyPermissionsTable.name)
                        .map {
                            it[MyUsersTable.username] to it[MyPermissionsTable.name]
                        }

                    result.forEachIndexed { idx, row ->
                        println("${idx + 1}. $row")
                    }

                }
            }
//            transaction {
//                val users = MyUsersTable.selectAll()
//                users.forEach { user ->
//                    println("USER: $user")
//                }
//
//            }
            setup()
            showSomeSql()
            populateTables()
            runQueries()
        }
    }

}