package com.abhiek.ezrecipes.data.models

data class PasskeyCreationOptions(
    val challenge: String,
    val rp: RelyingParty,
    val user: User,
    val pubKeyCredParams: List<PubKeyCredParam>,
    val timeout: Int,
    val attestation: String,
    val excludeCredentials: List<Credential>,
    val authenticatorSelection: Map<String, String>,
    val extensions: Map<String, Boolean>,
    val hints: List<String>
) {
    data class RelyingParty(
        val name: String,
        val id: String
    )

    data class User(
        val id: String,
        val name: String,
        val displayName: String
    )

    data class PubKeyCredParam(
        val alg: Int,
        val type: String
    )

    data class Credential(
        val id: String,
        val transports: List<String>,
        val type: String
    )
}
