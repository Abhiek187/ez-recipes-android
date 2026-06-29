package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.Serializable

@Serializable
data class RememberMe(
    val username: String,
    val expireAt: Long
)
