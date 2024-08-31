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
 * CollaberaInterview test
 * based on interview 22-Aug-2024, Thursday, 9:00AM-9:45AM
 * administered by COGBEE on behalf of Collabera
 *
 * Employee Salary Problem
 *   You have a database table : Employee with employee_id, name, and salary
 *   List the names of the employees having the 4th highest salary
 *
    SELECT name, salary  -- show name and salary
    FROM employees
    WHERE salary = (
      SELECT DISTINCT salary
      FROM employees
      ORDER BY salary DESC
      OFFSET 3  -- skip the first 3 (we want the 4th only)
      LIMIT 1   -- take only the next one, which is the 4th one
);
 *
 * Student Department Problem
 *   You have 2 database tables:
 *     1) Student : id, name, list of department ids
 *     2) Department : id, name
 *
 */
class CollaberaInterviewTest {
    // Tables
    object Employees : IntIdTable() {
        var name = varchar("name", 50)
        var salary = double("salary")
    }

    @Test
    fun `h2 - employee salary problem`() =
        `exercise employee salary problem`(Application::h2CollaberaInterview)

    @Test
    fun `postgres - employee salary problem`() =
        `exercise employee salary problem`(Application::postgresCollaberaInterview)

    private fun `exercise employee salary problem`(moduleFunction: Application.() -> Unit) {
        withTestApplication(moduleFunction) {
            // create tables
            transaction {
                addLogger(StdOutSqlLogger)
                val tables = arrayOf(Employees)
                SchemaUtils.drop(*tables)
                SchemaUtils.create(*tables)
                SchemaUtils.listDatabases().forEach { db ->
                    println("DATABASE: $db")
                }
            }

            // populate tables
            fun addEmployee(name: String, salary: Double) {
                transaction {
                    Employees.insertAndGetId {
                        it[Employees.name] = name
                        it[Employees.salary] = salary
                    }
                }
            }
            addEmployee("John", 95_000.00)
            addEmployee("Wilma", 95_000.00)
            addEmployee("Helen", 120_000.00)
            addEmployee("Bob", 95_000.00)
            addEmployee("Thomas", 65_000.00)
            addEmployee("Jamie", 95_000.00)
            addEmployee("Tabatha", 65_000.00)
            addEmployee("Candy", 110_000.00)

            transaction {
                // Subquery to find the fourth highest salary
                val fourthHighestSalary = Employees.select(Employees.salary)
                    .withDistinct()
                    .orderBy(Employees.salary, SortOrder.DESC)
                    .limit(1, offset = 3)  // Skip the first 3 to get the 4th highest salary
                    .map { it[Employees.salary] }
                    .firstOrNull()

                // Main query to select the employees with the fourth highest salary
                if (fourthHighestSalary != null) {
                    val listOfAnswers = mutableListOf<Pair<String, Double>>()
                    Employees.select(Employees.name, Employees.salary)
                        .where { Employees.salary eq fourthHighestSalary }
                        .forEach { row ->
                            println("Name: ${row[Employees.name]}, Salary: ${row[Employees.salary]}")
                            listOfAnswers.add(row[Employees.name] to row[Employees.salary])
                        }
                    assertEquals(2, listOfAnswers.size)
                    assertEquals(65_000.00, listOfAnswers[0].second)
                } else {
                    println("No fourth highest salary found.")
                }
            }
        }
    }

}

fun Application.h2CollaberaInterview() {
    // setup everything
    fun h2Database(clearDatabase: Boolean = false): Database {
        val databaseFile =
            File("./build/h2CollaberaInterview") // results in PROJECT/app/build/db.mv.db
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

fun Application.postgresCollaberaInterview() {
    fun postgresDatabase(): Database {
        return Database.connect(
            "jdbc:postgresql://localhost:5432/collabera",
            user = "collabera",
            password = "collabera"
        )
    }
    postgresDatabase()

}