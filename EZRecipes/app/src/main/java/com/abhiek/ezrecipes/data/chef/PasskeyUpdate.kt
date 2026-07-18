package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class PasskeyUpdate(
    val id: String,
    val name: String
)
