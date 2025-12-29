package com.abhiek.ezrecipes.data.models

data class OAuthRequest(
    val code: String,
    // Request bodies don't automatically serialize enums using toString()
    val providerId: String,
    val redirectUrl: String
)
