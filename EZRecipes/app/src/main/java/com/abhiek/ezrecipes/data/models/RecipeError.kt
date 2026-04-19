package com.abhiek.ezrecipes.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RecipeError(
    val error: String
)