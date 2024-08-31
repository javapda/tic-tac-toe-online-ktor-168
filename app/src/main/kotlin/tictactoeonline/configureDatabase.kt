package tictactoeonline

import io.ktor.application.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object MyUser : IntIdTable() {
    //    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val password: Column<String> = varchar("password", 50)
    val amount: Column<Int> = integer("amount")
}

fun Application.configureDatabase(clearDatabase: Boolean = false) {
//    val databaseFile = File("./build/db.mv.db")
    val databaseFile = File("./build/db") // results in PROJECT/app/build/db.mv.db
    val databaseFileOnFileSystem = File("${databaseFile.absoluteFile}.mv.db")
    if (clearDatabase) {
        if (databaseFileOnFileSystem.exists()) {
            databaseFileOnFileSystem.delete()
        }
    }
//    val db = Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
//    val db = Database.connect("jdbc:h2:./build/db.mv.db", driver = "org.h2.Driver")
    val db = Database.connect("jdbc:h2:${databaseFile.absoluteFile}", driver = "org.h2.Driver")
//    // jdbc:h2:./data/
//    // jdbc:h2:build/db.mv.db
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(MyUser)
    }
}