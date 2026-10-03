package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PasskeyClientResponse<R: PasskeyClientResponse.Response>(
    val authenticatorAttachment: String,
    val clientExtensionResults: Map<String, JsonElement>, // JsonElement = Any
    val id: String,
    val rawId: String,
    val response: R,
    val type: String
) {
    interface Response {
        val authenticatorData: String? // not included when creating restore keys
        val clientDataJSON: String
    }

    @Serializable
    data class NewPasskeyResponse(
        val attestationObject: String,
        override val authenticatorData: String? = null,
        override val clientDataJSON: String,
        val publicKey: String,
        val publicKeyAlgorithm: Int,
        val transports: List<String>
    ): Response

    @Serializable
    data class ExistingPasskeyResponse(
        override val authenticatorData: String,
        override val clientDataJSON: String,
        val signature: String,
        val userHandle: String
    ): Response
}

typealias NewPasskeyClientResponse = PasskeyClientResponse<
        PasskeyClientResponse.NewPasskeyResponse>
typealias ExistingPasskeyClientResponse = PasskeyClientResponse<
        PasskeyClientResponse.ExistingPasskeyResponse>
