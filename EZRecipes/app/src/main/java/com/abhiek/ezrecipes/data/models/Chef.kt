package com.abhiek.ezrecipes.data.models

data class Chef(
    val uid: String,
    val email: String,
    val ratings: Map<String, Int>,
    val recentRecipes: Map<String, String>,
    val favoriteRecipes: List<String>,
    val token: String
)