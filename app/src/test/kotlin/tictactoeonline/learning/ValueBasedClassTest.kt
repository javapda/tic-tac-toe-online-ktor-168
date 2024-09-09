package tictactoeonline.learning

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Value based class test
 * https://kotlinlang.org/docs/inline-classes.html
 *
 * An inline class must have a single property initialized in the primary constructor.
 * At runtime, instances of the inline class will be represented using this single
 * property (see details about runtime representation below):
 *
 * // No actual instantiation of class 'Password' happens
 * // At runtime 'securePassword' contains just 'String'
 * val securePassword = Password("Don't try this in production")
 */
class ValueBasedClassTest {
    @JvmInline
    value class Password(val value: String)

    @Test
    fun `first test of inline value class`() {
        val pw = Password("jed")
        assertEquals("jed", pw.value)
    }
}