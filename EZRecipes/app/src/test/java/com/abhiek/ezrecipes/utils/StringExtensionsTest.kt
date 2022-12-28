package com.abhiek.ezrecipes.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StringExtensionsTest {
    @Test
    fun capitalizeWords() {
        // Given a string
        val testString = "sugar, Spice, and EVERYTHING nIcE 1234"
        // When .capitalizeWords() is called
        val actualString = testString.capitalizeWords()
        // Then the first letter of each word should be in uppercase
        assertEquals("Sugar, Spice, And EVERYTHING NIcE 1234", actualString)
    }

    @Test
    fun capitalizeWordsWithEmptyString() {
        // Given an empty string
        val testString = ""
        // When .capitalizeWords() is called
        val actualString = testString.capitalizeWords()
        // Then the output should be the same
        assertEquals("", actualString)
    }

    @Test
    fun capitalizeWordsWithOnlySpaces() {
        // Given a string of spaces
        val testString = "   "
        // When .capitalizeWords() is called
        val actualString = testString.capitalizeWords()
        // Then the output should be the same
        assertEquals("   ", actualString)
    }
}
