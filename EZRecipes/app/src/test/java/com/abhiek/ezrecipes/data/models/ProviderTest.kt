package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ProviderTest {
    @Test
    fun testValueOfOrNull() {
        assertEquals(
            Provider.GOOGLE,
            Provider.valueOfOrNull("google.com")
        )
        assertEquals(
            Provider.FACEBOOK,
            Provider.valueOfOrNull("facebook.com")
        )
        assertEquals(
            Provider.GITHUB,
            Provider.valueOfOrNull("github.com")
        )
        assertNull(Provider.valueOfOrNull("password"))
    }
}
