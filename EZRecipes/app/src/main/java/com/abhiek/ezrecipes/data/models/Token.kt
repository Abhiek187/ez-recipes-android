package com.abhiek.ezrecipes.data.models

data class Token(
    // The token may not be present if it wasn't passed in the request
    var token: String? = null
)
