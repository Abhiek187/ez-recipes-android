package com.abhiek.ezrecipes.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

    @ParameterizedTest
    @CsvSource(
        // UNIX = Date.parse(ISO)
        // ISO = new Date(UNIX).toISOString()
        "2024-10-17T02:54:07.471Z,1729133647471",
        "2024-10-17T22:28:27.387Z,1729204107387",
        "2025-02-02T22:56:02.697Z,1738536962697",
        "1970-01-01T00:00:00.000Z,0",
        "1969-12-31T23:59:59.999Z,-1",
        "2038-01-19T03:14:08.000Z,2147483648000",
        "not-a-real:date," // empty = null
    )
    fun toUnixTimestamp(isoString: String, expectedTimestamp: Long?) {
        // Given an ISO string
        // When converted to a Unix timestamp
        val actualTimestamp = isoString.toUnixTimestamp()
        // Then it should match the expected value
        assertEquals(expectedTimestamp, actualTimestamp)
    }

    @ParameterizedTest
    @CsvSource(
        "2024-10-17T02:54:07.471Z,true",
        "2024-10-17T22:28:27.387Z,true",
        "2025-02-02T22:56:02.697Z,true",
        "1970-01-01T00:00:00.000Z,true",
        "1969-12-31T23:59:59.999Z,true",
        "2038-01-19T03:14:08.000Z,true",
        "not-a-real:date,false"
    )
    fun toDateTime(isoString: String, isValid: Boolean) {
        // Given an ISO string
        // When converted to a human-readable datetime
        val datetime = isoString.toDateTime()
        // Then it shouldn't match the original string
        // Due to locale differences, we can't check for specific output strings
        assertEquals(isValid, isoString != datetime)
    }
}
