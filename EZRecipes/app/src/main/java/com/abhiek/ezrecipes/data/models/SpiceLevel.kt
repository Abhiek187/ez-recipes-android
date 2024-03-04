package com.abhiek.ezrecipes.data.models

enum class SpiceLevel {
    NONE, MILD, SPICY, UNKNOWN;

    override fun toString() = name.lowercase()
}
