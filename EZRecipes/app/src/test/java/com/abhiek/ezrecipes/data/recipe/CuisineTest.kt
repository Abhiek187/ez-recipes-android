package com.abhiek.ezrecipes.data.recipe

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CuisineTest {
    @Test
    fun testToString() {
        Assertions.assertEquals("African", Cuisine.AFRICAN.toString())
        Assertions.assertEquals("Mediterranean", Cuisine.MEDITERRANEAN.toString())
        Assertions.assertEquals("Unknown", Cuisine.UNKNOWN.toString())
        Assertions.assertEquals("bbq", Cuisine.BBQ.toString())

        Assertions.assertEquals("Eastern European", Cuisine.EASTERN_EUROPEAN.toString())
        Assertions.assertEquals("Latin American", Cuisine.LATIN_AMERICAN.toString())
        Assertions.assertEquals("Middle Eastern", Cuisine.MIDDLE_EASTERN.toString())
    }
}