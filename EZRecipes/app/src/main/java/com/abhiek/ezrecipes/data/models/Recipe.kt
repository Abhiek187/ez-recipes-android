package com.abhiek.ezrecipes.data.models

data class Recipe(
    val _id: String?,
    val id: Int,
    val name: String,
    val url: String?,
    val image: String,
    val credit: String,
    val sourceUrl: String,
    val healthScore: Int,
    val time: Int,
    val servings: Int,
    val summary: String,
    val types: List<MealType>,
    val spiceLevel: SpiceLevel,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isHealthy: Boolean,
    val isCheap: Boolean,
    val isSustainable: Boolean,
    val culture: List<Cuisine>,
    val nutrients: List<Nutrient>,
    val ingredients: List<Ingredient>,
    val instructions: List<Instruction>,
    var token: String? = null, // searchSequenceToken for pagination
    val totalRatings: Int? = null,
    val averageRating: Double? = null,
    val views: Int? = null
)
