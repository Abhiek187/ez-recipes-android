package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String
)
