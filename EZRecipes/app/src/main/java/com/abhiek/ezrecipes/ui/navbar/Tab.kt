package com.abhiek.ezrecipes.ui.navbar

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.utils.Constants

// Routes and their associated titles and icons in the navigation graph
sealed class Tab(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    // Icons: https://fonts.google.com/icons (some require material-icons-extended)
    data object Home: Tab(Constants.Routes.HOME, R.string.home_tab, Icons.Filled.Home)
    data object Search: Tab(Constants.Routes.SEARCH, R.string.search_tab, Icons.Filled.Search)
    data object Glossary: Tab(Constants.Routes.GLOSSARY, R.string.glossary_tab,
        Icons.AutoMirrored.Filled.MenuBook
    )
}
