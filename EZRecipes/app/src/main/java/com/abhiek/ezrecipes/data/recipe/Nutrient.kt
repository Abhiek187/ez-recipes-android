package com.abhiek.ezrecipes.data.recipe

import androidx.appfunctions.AppFunctionSerializable
import kotlinx.serialization.Serializable

/** Nutritional information about a recipe */
@AppFunctionSerializable(isDescribedByKDoc = true)
@Serializable
data class Nutrient(
    /** Nutrient name */
    val name: String,
    /** Numerical amount of the nutrient */
    val amount: Double,
    /** Units to measure each nutrient */
    val unit: String
)
