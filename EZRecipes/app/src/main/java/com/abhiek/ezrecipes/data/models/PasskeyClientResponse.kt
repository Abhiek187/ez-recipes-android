package com.abhiek.ezrecipes.data.models

data class PasskeyClientResponse(
    val authenticatorAttachment: String,
    val clientExtensionResults: Map<String, Any>,
    val id: String,
    val rawId: String,
    val response: Response,
    val type: String
) {
    interface Response {
        val authenticatorData: String
        val clientDataJSON: String
    }

    data class NewPasskeyResponse(
        val attestationObject: String,
        override val authenticatorData: String,
        override val clientDataJSON: String,
        val publicKey: String,
        val publicKeyAlgorithm: Int,
        val transports: List<String>
    ): Response

    data class ExistingPasskeyResponse(
        override val authenticatorData: String,
        override val clientDataJSON: String,
        val signature: String,
        val userHandle: String
    ): Response
}
