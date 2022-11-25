package com.abhiek.ezrecipes.ui.navbar

// Routes and their associated titles in the navigation graph
sealed class DrawerItem(val route: String, val title: String) {
    object Home: DrawerItem("home", "Home")
    object Recipe: DrawerItem("recipe/{recipe}", "")
}
