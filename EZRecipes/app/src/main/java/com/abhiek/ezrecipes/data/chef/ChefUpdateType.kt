package com.abhiek.ezrecipes.data.chef

import kotlinx.serialization.SerialName

enum class ChefUpdateType {
    @SerialName("email")
    EMAIL,
    @SerialName("password")
    PASSWORD
}
