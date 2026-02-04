package com.abhiek.ezrecipes.data.models

data class Passkey(
    val webAuthnUserID: String? = null,
    val id: String,
    val publicKey: String,
    val counter: Int,
    val transports: List<String>? = null,
    val deviceType: String,
    val backedUp: Boolean,
    val name: String,
    val lastUsed: String,
    val iconLight: String? = null,
    val iconDark: String? = null
)
