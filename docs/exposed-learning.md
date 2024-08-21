# exposed-learning | [readme](../readme.md)

## resource
* [Exposed docs](https://jetbrains.github.io/Exposed)
* [problem with H2 version](https://stackoverflow.com/questions/40729216/h2-database-unsupported-database-file-version-or-invalid-file-header-in-file)
  * when going to a different version of H2 (to 2.2.224 from 2.1.214) I ran into a version conflict with a H2 database file
that was created with the earlier version. To remedy, delete and recreate the file. 
    ```text
    2024-08-19 08:17:07.637 [Test worker] WARN  Exposed - Transaction attempt #0 failed: Unsupported database file version or invalid file header in file "C:/Users/javap/tic-tac-toe-online-ktor-168/app/build/db.mv.db" [90048-224]. Statement(s): null
    org.h2.jdbc.JdbcSQLNonTransientConnectionException: Unsupported database file version or invalid file header in file "C:/Users/javap/tic-tac-toe-online-ktor-168/app/build/db.mv.db" [90048-224]
    ```
  * NOTE:
    the H2 database created a file called db.trace.db that described the situation.
    ```text
    2024-08-19 08:14:21.835615-07:00 database: flush
    org.h2.message.DbException: General error: "org.h2.mvstore.MVStoreException: The write format 2 is smaller than the supported format 3 [2.2.224/5]" [50000-224]
    at org.h2.message.DbException.get(DbException.java:212)
    ```


## dependencies
* gradle.properties : to set the desired version variables
```text
exposedVersion=0.53.0
h2DriverVersion=2.2.224
#h2DriverVersion=2.1.214  # note the 2.2.224 gave an invalid file format issue
logbackVersion=1.4.14
```
* build.gradle : where dependencies are set
```text

dependencies {
    implementation("com.h2database:h2:$h2DriverVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation "ch.qos.logback:logback-classic:$logbackVersion"

}
```