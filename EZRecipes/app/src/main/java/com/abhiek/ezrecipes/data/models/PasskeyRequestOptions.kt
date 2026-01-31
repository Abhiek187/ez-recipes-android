package com.abhiek.ezrecipes.data.models

data class PasskeyRequestOptions(
    val rpId: String,
    val challenge: String,
    val allowCredentials: List<Credential>,
    val timeout: Int,
    val userVerification: String
) {
    data class Credential(
        val id: String,
        val transports: List<String>,
        val type: String
    )
}
