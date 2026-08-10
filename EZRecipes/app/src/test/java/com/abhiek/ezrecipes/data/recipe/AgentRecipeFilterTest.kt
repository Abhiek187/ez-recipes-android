package com.abhiek.ezrecipes.data.recipe

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AgentRecipeFilterTest {
    @Test
    fun toRecipeFilter() {
        // Given an agent recipe filter
        val agentRecipeFilter = AgentRecipeFilter(
            query = "pizza",
            maxCals = 800,
            vegetarian = true,
            vegan = true,
            rating = 4,
            spiceLevel = listOf("mild", "spicy"),
            type = listOf("main course", "lunch", "hor d'oeuvre"),
            culture = listOf("Indian", "Mexican", "Latin American", "bbq")
        )
        // When converted into a regular recipe filter
        val recipeFilter = agentRecipeFilter.toRecipeFilter()
        // Then all the fields are deserialized correctly
        assertEquals(recipeFilter, RecipeFilter(
            query = "pizza",
            maxCals = 800,
            vegetarian = true,
            vegan = true,
            rating = 4,
            spiceLevel = listOf(SpiceLevel.MILD, SpiceLevel.SPICY),
            type = listOf(MealType.MAIN_COURSE, MealType.LUNCH, MealType.HOR_D_OEUVRE),
            culture = listOf(Cuisine.INDIAN, Cuisine.MEXICAN, Cuisine.LATIN_AMERICAN, Cuisine.BBQ)
        ))
    }
}
