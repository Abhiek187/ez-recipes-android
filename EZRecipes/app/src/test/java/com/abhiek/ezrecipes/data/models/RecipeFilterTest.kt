package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RecipeFilterTest {
    @Test
    fun toMap() {
        // Given a RecipeFilter with all params
        val recipeFilter = RecipeFilter(
            query = "pasta",
            minCals = 500,
            maxCals = 800,
            vegetarian = true,
            vegan = true,
            glutenFree = true,
            healthy = true,
            cheap = false,
            sustainable = false,
            rating = 3,
            spiceLevel = listOf(SpiceLevel.MILD, SpiceLevel.SPICY),
            type = listOf(MealType.ANTIPASTI),
            culture = listOf(Cuisine.ITALIAN)
        )

        // When converted to a map
        val recipeFilterMap = recipeFilter.toMap()

        // Then it should omit certain keys
        assertEquals(mapOf(
            "query" to recipeFilter.query,
            "min-cals" to recipeFilter.minCals?.toLong(),
            "max-cals" to recipeFilter.maxCals?.toLong(),
            "rating" to recipeFilter.rating?.toLong()
        ), recipeFilterMap)
    }
}
