package com.abhiek.ezrecipes.data.terms

import kotlinx.serialization.Serializable

@Serializable
data class TermStore(
    val terms: List<Term>,
    val expireAt: Long
)
