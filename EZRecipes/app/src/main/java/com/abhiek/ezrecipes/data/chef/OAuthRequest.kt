package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class OAuthRequest(
    val code: String,
    val providerId: Provider,
    val redirectUrl: String
)
