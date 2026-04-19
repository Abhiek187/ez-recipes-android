package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.serializers.SpiceLevelSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SpiceLevelSerializer::class)
enum class SpiceLevel {
    NONE, MILD, SPICY, UNKNOWN;

    override fun toString() = name.lowercase()
}
