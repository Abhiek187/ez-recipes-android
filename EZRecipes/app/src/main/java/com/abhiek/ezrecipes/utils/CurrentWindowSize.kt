package com.abhiek.ezrecipes.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Gets the size class of the current device
 *
 * Source: https://stackoverflow.com/a/77903185
 *
 * @return the WindowSizeClass width & height: either Compact, Medium, or Expanded
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun currentWindowSize(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val size = DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
    return WindowSizeClass.calculateFromSize(size)
}