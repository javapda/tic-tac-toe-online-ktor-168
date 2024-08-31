# gradle-notes | [readme](../readme.md)

## resources
* [kotlin configure gradle project](https://kotlinlang.org/docs/gradle-configure-project.html)

## kotlin vs groovy
* You can place properties in `gradle.properties` and access them in your gradle build scripts.
The way you do it depends on whether you are working with Kotlin gradle scripts (build.gradle.kts)
or groovy gradle scripts (build.gradle).
* Let's add an entry in `gradle.properties` of `exposedVersion=0.53.0` and let's refer to it in our
gradle build scripts:
  * for kotlin (build.gradle.kts)
    ```text
        // you need to first declare it from the project
        val exposedVersion: String by project
        ...
        dependencies {
            implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
        }
    ```
  * for groovy (build.gradle) - you simply refer to it with the $template (i.e. $exposedVersion)
    ```text
        dependencies {
            implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
        }
    ```    