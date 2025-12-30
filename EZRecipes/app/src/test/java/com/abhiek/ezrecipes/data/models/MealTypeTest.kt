package com.abhiek.ezrecipes.data.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MealTypeTest {
    @Test
    fun testToString() {
        assertEquals("fingerfood", MealType.FINGERFOOD.toString())
        assertEquals("antipasti", MealType.ANTIPASTI.toString())
        assertEquals("unknown", MealType.UNKNOWN.toString())

        assertEquals("main course", MealType.MAIN_COURSE.toString())
        assertEquals("main dish", MealType.MAIN_DISH.toString())
        assertEquals("morning meal", MealType.MORNING_MEAL.toString())

        assertEquals("hor d'oeuvre", MealType.HOR_D_OEUVRE.toString())
    }
}
