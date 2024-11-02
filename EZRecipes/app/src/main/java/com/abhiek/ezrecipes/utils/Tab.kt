package com.abhiek.ezrecipes.utils

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.abhiek.ezrecipes.R

// Routes and their associated titles and icons in the navigation graph
sealed class Tab(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    // Icons: https://fonts.google.com/icons (some require material-icons-extended)
    data object Home: Tab(Routes.HOME, R.string.home_tab, Icons.Filled.Home)
    data object Search: Tab(Routes.SEARCH, R.string.search_tab, Icons.Filled.Search)
    data object Glossary: Tab(Routes.GLOSSARY, R.string.glossary_tab,
        Icons.AutoMirrored.Filled.MenuBook)
    data object Profile: Tab(Routes.PROFILE, R.string.profile_tab,
        Icons.Filled.AccountCircle)
}
