# postgres | [readme](../readme.md)

## resources
* [ktor postgres](https://ktor.io/docs/server-integrate-database.html#add-postgresql-repository)
* [psql](D:\Program Files\PostgreSQL\16\bin)
* [kotlin-exposed on stackoverflow](https://stackoverflow.com/questions/tagged/kotlin-exposed)
* SQLDeveloper : [download](https://www.oracle.com/database/sqldeveloper/technologies/download/)|[article](https://www.enterprisedb.com/postgres-tutorials/how-connect-postgresql-using-sql-developer-visual-studio-and-dbeaver)
* [postgres jdbc driver download](https://jdbc.postgresql.org/download/)
* [postgres keywords (including USER)](https://www.postgresql.org/docs/current/sql-keywords-appendix.html)

## issues
### unable to drop table `user`
* created a table called 'user' in postgres, but when went to drop it with `drop table user;` was getting the following
error:
```
ERROR:  syntax error at or near "user"
LINE 1: drop table user;
                   ^ 

SQL state: 42601
Character: 12 
```
* The issue I was encountering happens because `user` is a reserved keyword in PostgreSQL. To avoid this 
syntax error, you need to tell PostgreSQL that you are referring to the table and not the keyword by 
quoting the table name. You can drop the table by using double quotes around user, like this:
```
drop table "user";
```

## log into postgres using psql
```
psql -U postgres
psql -U gamedb_owner
```
## dependencies
```kotlin
dependencies {
    implementation("org.postgresql:postgresql:42.5.1")
}
```
## configureDatabases
```
fun Application.configureDatabases() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/gamedb",
        user = "gamedb_owner",
        password = "gamedb"
    )
}
```
## create user: gamedb
```
CREATE ROLE gamedb WITH
  LOGIN
  SUPERUSER
  INHERIT
  CREATEDB
  CREATEROLE
  NOREPLICATION
  BYPASSRLS
  ENCRYPTED PASSWORD 'SCRAM-SHA-256$4096:AyoLxN5rLQBMMlM/Z65RHg==$DitJ8I1d8UGgySONnyQRETja/txVR/Os2dz78bFX84c=:4AelrL2TqXY58ke2QA/G6NWwdZkiTsRkT8W3tKLsOcg=';

COMMENT ON ROLE gamedb IS 'owner of gamedb database';
```

## create database: gamedb
```
CREATE DATABASE gamedb
    WITH
    OWNER = gamedb
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE gamedb
    IS 'holds the game information for the Tic-Tac-Toe Online by Hyperskill';
```