package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RecipeSortFieldTest {
    @Test
    fun testToString() {
        assertEquals("Calories", RecipeSortField.CALORIES.toString())
        assertEquals("Health Score", RecipeSortField.HEALTH_SCORE.toString())
        assertEquals("Rating", RecipeSortField.RATING.toString())
        assertEquals("Views", RecipeSortField.VIEWS.toString())
    }
}
