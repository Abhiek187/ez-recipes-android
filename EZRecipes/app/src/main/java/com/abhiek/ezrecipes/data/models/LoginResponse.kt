package com.abhiek.ezrecipes.data.models

data class LoginResponse(
    val uid: String,
    val token: String,
    val emailVerified: Boolean
)
