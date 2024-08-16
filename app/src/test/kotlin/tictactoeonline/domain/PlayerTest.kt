package tictactoeonline.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayerTest {

    @Test
    fun getMarker() {
        assertEquals("X", Player("Jed", 'X').marker.toString())
        assertEquals("Jed", Player("Jed", 'X').name)
    }
}