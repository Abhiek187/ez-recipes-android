package com.abhiek.ezrecipes.data.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class ProviderStyle(
    val label: String,
    val backgroundColor: Color,
    val contentColor: Color,
    @param:DrawableRes val icon: Int
)
