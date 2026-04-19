package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<StepItem>,
    val equipment: List<StepItem>
)
