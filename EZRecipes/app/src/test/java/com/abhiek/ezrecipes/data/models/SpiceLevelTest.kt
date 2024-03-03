package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SpiceLevelTest {
    @Test
    fun testToString() {
        assertEquals("none", SpiceLevel.NONE.toString())
        assertEquals("mild", SpiceLevel.MILD.toString())
        assertEquals("spicy", SpiceLevel.SPICY.toString())
        assertEquals("unknown", SpiceLevel.UNKNOWN.toString())
    }
}
