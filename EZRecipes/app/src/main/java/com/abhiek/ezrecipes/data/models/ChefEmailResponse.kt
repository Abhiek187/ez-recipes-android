package com.abhiek.ezrecipes.data.models

data class ChefEmailResponse(
    val kind: String,
    val email: String,
    var token: String? = null
)
