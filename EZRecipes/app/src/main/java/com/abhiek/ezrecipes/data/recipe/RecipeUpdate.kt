package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeUpdate(
    val rating: Int? = null,
    val view: Boolean? = null,
    val isFavorite: Boolean? = null
)
