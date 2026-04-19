package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Instruction(
    val name: String,
    val steps: List<Step>
)
