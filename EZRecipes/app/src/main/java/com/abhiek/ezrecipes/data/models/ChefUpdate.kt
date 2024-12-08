package com.abhiek.ezrecipes.data.models

data class ChefUpdate(
    val type: ChefUpdateType,
    val email: String,
    var password: String? = null
)
