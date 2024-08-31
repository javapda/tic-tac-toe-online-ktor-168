package tictactoeonline.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tictactoeonline.User

class PlayerTest {

    @Test
    fun getMarker() {
        fun user() = User("Jed@someplace.com", "pwd")
        assertEquals("X", Player(user(), 'X').marker.toString())
        assertEquals("Jed@someplace.com", Player(user(), 'X').name)
    }

}