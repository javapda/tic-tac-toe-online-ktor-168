package tictactoeonline.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MyStringToolsTest {
    @Test
    fun `test `() {
        assertEquals(3, MyStringTools.randomStringByKotlinRandom(3).length)
    }
}