package com.abhiek.ezrecipes.data.models

data class OAuthRequest(
    val code: String,
    val providerId: Provider,
    val redirectUrl: String
)
