package com.abhiek.ezrecipes.data.chef

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ProviderTest {
    @Test
    fun testValueOfOrNull() {
        Assertions.assertEquals(
            Provider.GOOGLE,
            Provider.valueOfOrNull("google.com")
        )
        Assertions.assertEquals(
            Provider.FACEBOOK,
            Provider.valueOfOrNull("facebook.com")
        )
        Assertions.assertEquals(
            Provider.GITHUB,
            Provider.valueOfOrNull("github.com")
        )
        Assertions.assertNull(Provider.valueOfOrNull("password"))
    }
}