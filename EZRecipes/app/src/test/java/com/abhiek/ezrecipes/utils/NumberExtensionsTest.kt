package com.abhiek.ezrecipes.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class NumberExtensionsTest {
    @ParameterizedTest
    @CsvSource(
        "0,0",
        "1,1",
        "999,999",
        "1000,1K",
        "1234,1.2K",
        "999999,999.9K",
        "1000000,1M",
        "1234567,1.2M",
        "999999999,999.9M",
        "1000000000,1B",
        "1234567890,1.2B"
    )
    fun toShorthand(inputNum: Int, expectedStr: String) {
        // Android methods can't be mocked, so this just tests the else block
        // Build.VERSION.SDK_INT will always be 0 in unit tests
        val actualStr = inputNum.toShorthand()
        assertEquals(expectedStr, actualStr)
    }

    @ParameterizedTest
    @CsvSource(
        "0,0,0",
        "0.5,0,1",
        "1.8175,2,1.82",
        "3.14159265359,5,3.14159",
        "4.2047,2,4.2",
        "666,1,666",
        "666,-1,?"
    )
    fun round(inputNum: Double, places: Int, expectedNum: String) {
        if (places < 0) {
            assertThrows(IllegalArgumentException::class.java) {
                inputNum.round(places)
            }
        } else {
            val actualNum = inputNum.round(places)
            assertEquals(expectedNum, actualNum)
        }
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
        "2038-01-19T03:14:08.000Z,2147483648000"
    )
    fun toISODateString(expectedISOString: String, timestamp: Long) {
        // Given a Unix timestamp
        // When converted to an ISO string
        val actualISOString = timestamp.toISODateString()
        // Then it should match the expected value
        assertEquals(expectedISOString, actualISOString)
    }
}
