package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String
)
