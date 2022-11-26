package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.ui.graphics.vector.ImageVector

// Routes and their associated titles and icons in the navigation graph
sealed class DrawerItem(val route: String, val title: String, val icon: ImageVector) {
    // Icons: https://fonts.google.com/icons (some require material-icons-extended)
    object Home: DrawerItem("home", "Home", Icons.Filled.Home)
    object Recipe: DrawerItem("recipe/{recipe}", "Recipe", Icons.Filled.MenuBook)
}
