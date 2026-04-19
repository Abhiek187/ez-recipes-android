package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class PasskeyCreationOptions(
    val challenge: String,
    val rp: RelyingParty,
    val user: User,
    val pubKeyCredParams: List<PubKeyCredParam>,
    val timeout: Int,
    val attestation: String,
    val excludeCredentials: List<Credential>,
    val authenticatorSelection: AuthenticatorSelection,
    val extensions: Map<String, Boolean>,
    val hints: List<String>
) {
    @Serializable
    data class RelyingParty(
        val name: String,
        val id: String
    )

    @Serializable
    data class User(
        val id: String,
        val name: String,
        val displayName: String
    )

    @Serializable
    data class PubKeyCredParam(
        val alg: Int,
        val type: String
    )

    @Serializable
    data class Credential(
        val id: String,
        val transports: List<String>,
        val type: String
    )

    @Serializable
    data class AuthenticatorSelection(
        val requireResidentKey: Boolean,
        val residentKey: String,
        val userVerification: String
    )
}
