package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.MainViewModelFactory
import com.abhiek.ezrecipes.ui.home.Home
import com.abhiek.ezrecipes.ui.recipe.Recipe

@Composable
fun NavigationGraph(navController: NavHostController) {
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory()
    )

    // Show the appropriate composable based on the current route, starting at the home screen
    // NavHostController is a subclass of NavController
    NavHost(
        navController = navController,
        startDestination = DrawerItem.Home.route
    ) {
        composable(DrawerItem.Home.route) {
            Home(viewModel) {
                navController.navigate(DrawerItem.Recipe.route) {
                    // Only have one copy of the recipe destination in the back stack
                    launchSingleTop = true
                }
            }
        }
        composable(DrawerItem.Recipe.route) {
            Recipe(viewModel)
        }
    }
}
