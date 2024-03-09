package com.abhiek.ezrecipes.data.models

data class RecipeFilter(
    var query: String = "",
    var minCals: Int? = null,
    var maxCals: Int? = null,
    var vegetarian: Boolean = false,
    var vegan: Boolean = false,
    var glutenFree: Boolean = false,
    var healthy: Boolean = false,
    var cheap: Boolean = false,
    var sustainable: Boolean = false,
    var spiceLevel: List<SpiceLevel> = listOf(),
    var type: List<String> = listOf(),
    var culture: List<String> = listOf()
)
