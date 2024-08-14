# project-dependencies | [main readme](../readme.md)

* [from Hyperskill Stage 3/5: Adding authorization and room mechanism](https://hyperskill.org/projects/366/stages/2168/implement)
* [Ktor 1.6.8 Docs](https://ktor.io/docs/old/welcome.html)
  * [install ktor plugin [NOT NEEDED]](https://ktor.io/docs/old/intellij-idea.html#install_plugin)
  * []()
  * []()
  * []()
  * []()

```
plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.jvm)
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.7.22'
    // Apply the application plugin to add support for building a CLI application in Java.
    id 'application'

}
```

```text
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib"
    implementation "io.ktor:ktor-server-core:1.6.8"
    implementation "io.ktor:ktor-server-netty:1.6.8"
    implementation "ch.qos.logback:logback-classic:1.2.10"
    implementation "io.ktor:ktor-serialization:1.6.8"
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"
    implementation "io.ktor:ktor-auth:1.6.8"
    implementation "io.ktor:ktor-auth-jwt:1.6.8"
    testImplementation "io.ktor:ktor-server-test-host:1.6.8"
    // John added the following to support @ParameterizedTest, etc.
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.3")
}
```