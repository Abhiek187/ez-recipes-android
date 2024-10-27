package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.MainViewModelFactory
import com.abhiek.ezrecipes.ui.glossary.Glossary
import com.abhiek.ezrecipes.ui.glossary.GlossaryViewModel
import com.abhiek.ezrecipes.ui.glossary.GlossaryViewModelFactory
import com.abhiek.ezrecipes.ui.home.Home
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.Profile
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.profile.ProfileViewModelFactory
import com.abhiek.ezrecipes.ui.recipe.Recipe
import com.abhiek.ezrecipes.ui.search.FilterForm
import com.abhiek.ezrecipes.ui.search.SearchResults
import com.abhiek.ezrecipes.ui.search.SearchViewModel
import com.abhiek.ezrecipes.ui.search.SearchViewModelFactory
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.*

@Composable
fun NavigationGraph(
    navController: NavHostController,
    widthSizeClass: WindowWidthSizeClass,
    startDestination: String = Constants.Routes.HOME
) {
    val context = LocalContext.current
    val isWideScreen = widthSizeClass == WindowWidthSizeClass.Expanded

    val mainViewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory(context)
    )
    val searchViewModel = viewModel<SearchViewModel>(
        factory = SearchViewModelFactory()
    )
    val glossaryViewModel = viewModel<GlossaryViewModel>(
        factory = GlossaryViewModelFactory(context)
    )
    val profileViewModel = viewModel<ProfileViewModel>(
        factory = ProfileViewModelFactory()
    )

    // Only call once when composed
    LaunchedEffect(Unit) {
        glossaryViewModel.checkCachedTerms()
    }

    // Show the appropriate composable based on the current route, starting at the home screen
    // NavHostController is a subclass of NavController
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            Constants.Routes.HOME,
            // Fading in is ok when switching tabs
            exitTransition = if (mainViewModel.recipe != null) {
                { slideLeftExit() }
            } else null,
            popEnterTransition = if (mainViewModel.recipe != null) {
                { slideRightEnter() }
            } else null
        ) {
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
            ),
            // Mimic sliding transitions on iOS
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = { slideRightEnter() },
            popExitTransition = { slideRightExit() }
        ) { backStackEntry ->
            Recipe(mainViewModel, isWideScreen, backStackEntry.arguments?.getString("id"))
        }
        composable(
            Constants.Routes.SEARCH,
            exitTransition = if (searchViewModel.recipes.isNotEmpty()) {
                { slideLeftExit() }
            } else null,
            popEnterTransition = if (searchViewModel.recipes.isNotEmpty()) {
                { slideRightEnter() }
            } else null
        ) {
            // On large screens, show the form and results side-by-side
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                FilterForm(searchViewModel) {
                    navController.navigate(Constants.Routes.RESULTS)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterForm(
                        searchViewModel,
                        modifier = Modifier.weight(1f)
                    ) {}
                    SearchResults(
                        mainViewModel,
                        searchViewModel,
                        modifier = Modifier.weight(
                            if (widthSizeClass == WindowWidthSizeClass.Medium) 1f else 2f
                        )
                    ) {
                        navController.navigate(
                            Constants.Routes.RECIPE.replace(
                                "{id}", mainViewModel.recipe?.id.toString()
                            )
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
        composable(
            Constants.Routes.RESULTS,
            enterTransition = { slideLeftEnter() },
            exitTransition = { slideLeftExit() },
            popEnterTransition = { slideRightEnter() },
            popExitTransition = { slideRightExit() }
        ) {
            SearchResults(mainViewModel, searchViewModel) {
                navController.navigate(
                    Constants.Routes.RECIPE.replace(
                        "{id}", mainViewModel.recipe?.id.toString()
                    )
                ) {
                    launchSingleTop = true
                }
            }
        }
        composable(
            Constants.Routes.GLOSSARY
        ) {
            Glossary(glossaryViewModel.terms)
        }
        composable(
            Constants.Routes.PROFILE
        ) {
            Profile(profileViewModel)
        }
    }
}

private class NavigationGraphPreviewParameterProvider: PreviewParameterProvider<String> {
    override val values = sequenceOf(
        Constants.Routes.HOME,
        Constants.Routes.RECIPE,
        Constants.Routes.SEARCH,
        Constants.Routes.RESULTS,
        Constants.Routes.GLOSSARY
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun NavigationGraphPreview(
    @PreviewParameter(NavigationGraphPreviewParameterProvider::class) route: String
) {
    val navController = rememberNavController()
    val windowSize = currentWindowSize()

    EZRecipesTheme {
        Surface {
            NavigationGraph(navController, windowSize.widthSizeClass, route)
        }
    }
}
