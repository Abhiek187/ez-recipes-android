package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val uid: String,
    val token: String,
    val emailVerified: Boolean
)
