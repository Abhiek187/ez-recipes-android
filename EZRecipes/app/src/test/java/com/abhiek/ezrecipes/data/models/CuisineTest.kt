package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CuisineTest {
    @Test
    fun testToString() {
        assertEquals("African", Cuisine.AFRICAN.toString())
        assertEquals("Mediterranean", Cuisine.MEDITERRANEAN.toString())
        assertEquals("Unknown", Cuisine.UNKNOWN.toString())
        assertEquals("BBQ", Cuisine.BBQ.toString())

        assertEquals("Eastern European", Cuisine.EASTERN_EUROPEAN.toString())
        assertEquals("Latin American", Cuisine.LATIN_AMERICAN.toString())
        assertEquals("Middle Eastern", Cuisine.MIDDLE_EASTERN.toString())
    }
}
