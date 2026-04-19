package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class ChefUpdate(
    val type: ChefUpdateType,
    val email: String,
    var password: String? = null
)
