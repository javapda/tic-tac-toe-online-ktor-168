# setup-project | [main readme](../readme.md)

## summary
* create initial directory, and traverse to that directory
* run _**gradle init**_
* run _**idea .**_

```
mkdir tic-tac-toe-online-ktor-168
cd tic-tac-toe-online-ktor-168
gradle init
# accept defaults, except use the Groovy version of the DSL


        C:\Users\myuser\tic-tac-toe-online-ktor-168>gradle init

        Select type of build to generate:
1: Application
2: Library
3: Gradle plugin
        4: Basic (build structure only)
Enter selection (default: Application) [1..4] 1

Select implementation language:
1: Java
2: Kotlin
3: Groovy
4: Scala
5: C++
6: Swift
Enter selection (default: Java) [1..6] 2

Enter target Java version (min: 7, default: 21):

Project name (default: tic-tac-toe-online-ktor-168):

Select application structure:
1: Single application project
2: Application and library project
        Enter selection (default: Single application project) [1..2] 1

Select build script DSL:
1: Kotlin
2: Groovy
Enter selection (default: Kotlin) [1..2] 2

Select test framework:
1: kotlin.test
2: JUnit Jupiter
        Enter selection (default: kotlin.test) [1..2] 1

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no]



> Task :init
To learn more about Gradle by exploring our Samples at https://docs.gradle.org/8.8/samples/sample_building_kotlin_applications.html

BUILD SUCCESSFUL in 25s
        1 actionable task: 1 executed

        C:\Users\myuser\tic-tac-toe-online-ktor-168>idea .

```