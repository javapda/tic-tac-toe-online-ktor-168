package tictactoeonline.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Serializable
data class TestStatusPayload(val status: String, val message: String = "Gorilla", val age: Int, val happy: Boolean)

class JsonTest {
    @Test
    fun `test some serialization of TestStatusPayload`() {
        val tsp =
            Json.decodeFromString<TestStatusPayload>("""{"happy":true,"status":"monkey","message":"Ape Planet","age":34}""")
        assertEquals("monkey", tsp.status)
        assertEquals("Ape Planet", tsp.message)
        assertEquals(34, tsp.age)
        assertTrue(tsp.happy)
    }

    @Test
    fun `test Box serialization`() {
        @Serializable
        data class Box(val status: String)

        val deserialized = Json.decodeFromString<Box>("""{"status":"jed"}""")
        assertEquals("jed", deserialized.status)

    }
}