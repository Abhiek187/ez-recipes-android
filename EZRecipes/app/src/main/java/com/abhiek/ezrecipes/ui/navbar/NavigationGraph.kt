package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.MainViewModelFactory
import com.abhiek.ezrecipes.ui.home.Home
import com.abhiek.ezrecipes.ui.recipe.Recipe
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun NavigationGraph(navController: NavHostController, widthSizeClass: WindowWidthSizeClass) {
    val viewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory()
    )
    val isWideScreen = widthSizeClass == WindowWidthSizeClass.Expanded

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
        composable(
            DrawerItem.Recipe.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "${Constants.RECIPE_WEB_ORIGIN}/recipe/{id}" }
            )
        ) { backStackEntry ->
            Recipe(viewModel, isWideScreen, backStackEntry.arguments?.getString("id"))
        }
    }
}
