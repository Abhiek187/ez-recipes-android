package com.abhiek.ezrecipes.utils

import com.abhiek.ezrecipes.data.models.Cuisine
import com.abhiek.ezrecipes.data.models.MealType
import com.abhiek.ezrecipes.data.models.SpiceLevel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ListExtensionsTest {
    @Test
    fun contentEqualsValid() {
        // Given equivalent arrays of enums
        val (spiceList1, spiceList2) = Pair(
            listOf(SpiceLevel.SPICY),
            listOf(SpiceLevel.SPICY)
        )
        val (mealTypeList1, mealTypeList2) = Pair(
            listOf(MealType.ANTIPASTI, MealType.SOUP, MealType.STARTER, MealType.SNACK, MealType.ANTIPASTO, MealType.APPETIZER, MealType.HOR_D_OEUVRE),
            listOf(MealType.ANTIPASTI, MealType.SOUP, MealType.STARTER, MealType.SNACK, MealType.ANTIPASTO, MealType.APPETIZER, MealType.HOR_D_OEUVRE)
        )
        val (cuisineList1, cuisineList2) = Pair(
            listOf(Cuisine.CENTRAL_AMERICAN, Cuisine.CARIBBEAN, Cuisine.CENTRAL_AMERICAN),
            listOf(Cuisine.CENTRAL_AMERICAN, Cuisine.CARIBBEAN, Cuisine.CENTRAL_AMERICAN)
        )

        // When comparing the arrays using contentEquals
        // Then it should return true
        assertTrue(spiceList1 contentEquals spiceList2)
        assertTrue(mealTypeList1 contentEquals mealTypeList2)
        assertTrue(cuisineList1 contentEquals cuisineList2)
    }

    @Test
    fun contentEqualsInvalid() {
        // Given different arrays of enums
        val (spiceList1, spiceList2) = Pair(
            listOf(SpiceLevel.MILD),
            listOf(SpiceLevel.NONE)
        )
        val (mealTypeList1, mealTypeList2) = Pair(
            listOf(MealType.ANTIPASTI, MealType.SOUP, MealType.STARTER, MealType.SNACK, MealType.ANTIPASTO, MealType.APPETIZER, MealType.HOR_D_OEUVRE),
            listOf(MealType.SOUP, MealType.STARTER, MealType.SNACK, MealType.ANTIPASTI, MealType.ANTIPASTO, MealType.APPETIZER, MealType.HOR_D_OEUVRE)
        )
        val (cuisineList1, cuisineList2) = Pair(
            listOf<Cuisine>(),
            listOf(Cuisine.CENTRAL_AMERICAN, Cuisine.CENTRAL_AMERICAN)
        )

        // When comparing the arrays using contentEquals
        // Then it should return false
        assertFalse(spiceList1 contentEquals spiceList2)
        assertFalse(mealTypeList1 contentEquals mealTypeList2)
        assertFalse(cuisineList1 contentEquals cuisineList2)
    }
}
