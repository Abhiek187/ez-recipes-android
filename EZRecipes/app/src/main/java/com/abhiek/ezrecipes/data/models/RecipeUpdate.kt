package com.abhiek.ezrecipes.data.models

data class RecipeUpdate(
    val rating: Int? = null,
    val view: Boolean? = null,
    val isFavorite: Boolean? = null
)
