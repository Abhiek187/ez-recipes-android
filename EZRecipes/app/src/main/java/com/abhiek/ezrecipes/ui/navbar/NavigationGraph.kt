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
import com.abhiek.ezrecipes.ui.search.FilterForm
import com.abhiek.ezrecipes.ui.search.SearchViewModel
import com.abhiek.ezrecipes.ui.search.SearchViewModelFactory
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun NavigationGraph(navController: NavHostController, widthSizeClass: WindowWidthSizeClass) {
    val mainViewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory()
    )
    val searchViewModel = viewModel<SearchViewModel>(
        factory = SearchViewModelFactory()
    )
    val isWideScreen = widthSizeClass == WindowWidthSizeClass.Expanded

    // Show the appropriate composable based on the current route, starting at the home screen
    // NavHostController is a subclass of NavController
    NavHost(
        navController = navController,
        startDestination = Constants.Routes.HOME
    ) {
        composable(Constants.Routes.HOME) {
            Home(mainViewModel) {
                navController.navigate(
                    Constants.Routes.RECIPE.replace(
                        "{id}", mainViewModel.recipe?.id.toString()
                    )
                ) {
                    // Only have one copy of the recipe destination in the back stack
                    launchSingleTop = true
                }
            }
        }
        composable(
            Constants.Routes.RECIPE,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "${Constants.RECIPE_WEB_ORIGIN}/${Constants.Routes.RECIPE}"
                }
            )
        ) { backStackEntry ->
            Recipe(mainViewModel, isWideScreen, backStackEntry.arguments?.getString("id"))
        }
        composable(Constants.Routes.SEARCH) {
            FilterForm(searchViewModel)
        }
    }
}
