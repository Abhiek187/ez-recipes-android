package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class PasskeyRequestOptions(
    val rpId: String,
    val challenge: String,
    val allowCredentials: List<Credential>,
    val timeout: Int,
    val userVerification: String
) {
    @Serializable
    data class Credential(
        val id: String,
        val transports: List<String>,
        val type: String
    )
}
