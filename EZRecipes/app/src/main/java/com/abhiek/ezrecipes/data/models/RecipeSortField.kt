package com.abhiek.ezrecipes.data.models

import com.abhiek.ezrecipes.utils.capitalizeWords

enum class RecipeSortField {
    CALORIES,
    HEALTH_SCORE,
    RATING,
    VIEWS;

    override fun toString(): String {
        return name.replace("_", " ").lowercase().capitalizeWords()
    }

    fun toQueryParam(): String {
        return name.replace("_", "-").lowercase()
    }
}
