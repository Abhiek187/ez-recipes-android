package com.abhiek.ezrecipes.data.models

data class RecipeFilter(
    val query: String?,
    val minCals: Int?,
    val maxCals: Int?,
    val vegetarian: Boolean?,
    val vegan: Boolean?,
    val glutenFree: Boolean?,
    val healthy: Boolean?,
    val cheap: Boolean?,
    val sustainable: Boolean?,
    val spiceLevel: List<SpiceLevel>,
    val type: List<String>,
    val culture: List<String>
)
