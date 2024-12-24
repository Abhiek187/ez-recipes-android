package com.abhiek.ezrecipes.utils

import org.junit.jupiter.api.Assertions.assertEquals
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
}
