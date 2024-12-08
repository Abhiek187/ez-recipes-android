package com.abhiek.ezrecipes.data.models

import com.google.gson.annotations.SerializedName

enum class ChefUpdateType {
    @SerializedName("email")
    EMAIL,
    @SerializedName("password")
    PASSWORD
}
