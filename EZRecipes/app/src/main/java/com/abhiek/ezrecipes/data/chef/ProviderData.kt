package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class ProviderData(
    var displayName: String? = null,
    val email: String,
    var phoneNumber: String? = null,
    var photoURL: String? = null,
    val providerId: String, // password auth can appear here
    val uid: String
)