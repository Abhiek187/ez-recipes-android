package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class StepItem(
    val id: Int,
    val name: String,
    val image: String
)
