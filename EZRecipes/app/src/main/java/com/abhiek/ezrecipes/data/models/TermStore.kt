package com.abhiek.ezrecipes.data.models

data class TermStore(
    val terms: List<Term>,
    val expireAt: Long
)
