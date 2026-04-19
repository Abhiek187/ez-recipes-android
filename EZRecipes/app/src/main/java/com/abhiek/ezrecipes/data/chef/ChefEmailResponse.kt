package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class ChefEmailResponse(
    val kind: String,
    val email: String,
    var token: String? = null
)