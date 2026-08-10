package com.abhiek.ezrecipes.data.recipe

import androidx.appfunctions.AppFunctionSerializable

/** Basic recipe information, used for previewing recipe details at a glance */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentRecipePreview(
    /** Unique ID for the recipe */
    val id: Int,
    /** Recipe name */
    val name: String,
    /** The time in minutes to make the recipe */
    val time: Int,
    /** Paragraph summarizing the recipe */
    val summary: String,
    /** Nutritional information about a recipe */
    val nutrients: List<Nutrient>,
    /** The total number of ratings provided for a recipe */
    val totalRatings: Int?,
    /** The average rating out of 5 for a recipe */
    val averageRating: Double?
)
