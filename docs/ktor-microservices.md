# ktor-microservices | [readme](../readme.md)

## What are microservices?
Microservices architecture is like breaking a big task into smaller, simpler jobs that each have a special role. Imagine you are building a huge toy city with different parts, like houses, roads, and parks. Instead of one person doing everything, you assign one person to build houses, another to make roads, and someone else for parks. Each person works independently but communicates when necessary to connect their parts to the rest of the city.

In microservices architecture, the “big task” is a large application, like a website or an app. Instead of building it as one giant, complex piece, it's split into smaller, simpler services (like building blocks). Each of these services has its own job to do, like handling user logins, processing payments, or managing inventory. They work independently but talk to each other when needed.

The benefit? If one part breaks (like the person building the roads), the rest of the city can still function, and you only need to fix the road-building part. Similarly, in microservices, if one service has a problem, the other services can keep working, making it easier to maintain and update.


## Example Scenario in Kotlin using Ktor
Imagine we're building a `User Service` that handles user-related actions like
registering a user and fetching user details.

### Step 1: Setting up the project
To begin, we need a Ktor project. Go to [Ktor Starter page](https://start.ktor.io/)
Select:
* Content Negotiation and Kotlinx.serialization (will get Routing, Content Negotiation, Serialization, logging, junit for testing)
```
dependencies {
    implementation("io.ktor:ktor-server-core:2.3.3")
    implementation("io.ktor:ktor-server-netty:2.3.3")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
}
```
### Step 2: Define a simple user model
We'll create a `User` data class to represent a user.
```
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val name: String, val email: String)
```
The `@Serializable` annotation is used with Kotlin's `kotlinx.serialization` to allow
us to easily convert our class to and from JSON.

### Step 3: Create a user repository (mock data)
To keep things simple, we'll use an in-memory list as our `database`. Could be swapped 
with an `Exposed`-based database access mechanism.
```
object UserRepository {
    private val users = mutableListOf<User>()

    fun addUser(user: User) {
        users.add(user)
    }

    fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    fun getAllUsers(): List<User> {
        return users
    }
}
```
### Step 4: Building the microservice with Ktor
Now we can define our microservice's HTTP routes. The following code sets up a 
Ktor server that allows us to register users and retrieve user data.
```
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json() // Use kotlinx.serialization for JSON
        }
        routing {
            route("/users") {

                // Get all users
                get {
                    call.respond(UserRepository.getAllUsers())
                }

                // Get a specific user by ID
                get("{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id != null) {
                        val user = UserRepository.getUserById(id)
                        if (user != null) {
                            call.respond(user)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "User not found")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                    }
                }

                // Register a new user
                post {
                    val user = call.receive<User>()
                    UserRepository.addUser(user)
                    call.respond(HttpStatusCode.Created, "User added")
                }
            }
        }
    }.start(wait = true)
}

```
### Step 5: Testing the Microservice
You can run the Ktor server and test it using a tool like Postman or `curl` or TestApplication
in the Ktor server test host API.
* Add a user:
```
curl -X POST http://localhost:8080/users \
        -H "Content-Type: application/json" \
        -d '{"id":1, "name": "Alice", "email": "alice@example.com"}'
```
* Get all users:
```
curl http://localhost:8080/users
```
* Get a user by ID:
```
curl http://localhost:8080/users/1
```
### EXPLANATION
1. Routes
2. Serialization
3. Repository
### How it fits into microservices
In a real-world microservice architecture, this "User Service" would be one of
many services, each responsible for a specific part of the overall system.
It could communicate with other services (like a payment service, order service, etc.)
via APIs or messaging systems.
Each service runs independently, making the whole system more flexible and scalable.


