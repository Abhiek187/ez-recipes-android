package com.abhiek.ezrecipes.data.models

data class Recipe(
    val id: Int,
    val name: String,
    val url: String,
    val image: String,
    val credit: String?,
    val sourceUrl: String,
    val healthScore: Int,
    val time: Int,
    val servings: Int,
    val summary: String,
    val nutrients: List<Nutrient>,
    val ingredients: List<Ingredient>,
    val instructions: List<Instruction>
)
