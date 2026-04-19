package com.abhiek.ezrecipes.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Term(
    val _id: String,
    val word: String,
    val definition: String
)
