package com.abhiek.ezrecipes.data.models

data class Step(
    val number: Int,
    val step: String,
    val ingredients: List<StepItem>,
    val equipment: List<StepItem>
)
