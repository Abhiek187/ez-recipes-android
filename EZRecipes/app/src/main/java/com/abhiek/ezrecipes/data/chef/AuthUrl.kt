package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class AuthUrl(
    val providerId: Provider,
    val authUrl: String
)