package tictactoeonline.domain

import tictactoeonline.User
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTest {

    @Test
    fun `test User and name`() {
        fun user() = User("someUser@some.com", "1234")
        assertEquals("someUser@some.com", user().email)
    }
}