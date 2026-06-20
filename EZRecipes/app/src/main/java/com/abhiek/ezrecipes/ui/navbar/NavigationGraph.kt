package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
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
import com.abhiek.ezrecipes.ui.util.LocalNavigationState
import com.abhiek.ezrecipes.ui.util.rememberNavigationState
import com.abhiek.ezrecipes.ui.util.toEntries
import com.abhiek.ezrecipes.utils.*

@Composable
fun NavigationGraph(
    widthSizeClass: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val isWideScreen = widthSizeClass == WindowWidthSizeClass.Expanded
    val navigationState = LocalNavigationState.current
    val navigator = LocalNavigator.current

    val mainViewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory(context)
    )
    val searchViewModel = viewModel<SearchViewModel>(
        factory = SearchViewModelFactory(context)
    )
    val glossaryViewModel = viewModel<GlossaryViewModel>(
        factory = GlossaryViewModelFactory(context)
    )
    val profileViewModel = viewModel<ProfileViewModel>(
        factory = ProfileViewModelFactory(context)
    )

    // Only call once when composed
    LaunchedEffect(Unit) {
        glossaryViewModel.checkCachedTerms()
    }

    val entryProvider = entryProvider {
        entry<Routes.Home>(
            metadata = metadata {
                // Fading in is ok when switching tabs
                if (mainViewModel.recipe != null) {
                    put(NavDisplay.TransitionKey) {
                        EnterTransition.None togetherWith slideLeftExit()
                    }
                    put(NavDisplay.PopTransitionKey) {
                        slideRightEnter() togetherWith ExitTransition.None
                    }
                }
            }
        ) {
            Home(mainViewModel, profileViewModel) { recipe ->
                mainViewModel.recipe = recipe
                navigator.navigate(Routes.Recipe(recipe.id))
            }
        }
        entry<Routes.Recipe>(
            // Mimic sliding transitions on iOS
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideLeftEnter() togetherWith slideLeftExit()
                }
                put(NavDisplay.PopTransitionKey) {
                    slideRightEnter() togetherWith slideRightExit()
                }
            }
        ) { key ->
            Recipe(
                mainViewModel,
                profileViewModel,
                isWideScreen,
                key.id
            )
        }
        entry<Routes.Search>(
            metadata = metadata {
                if (searchViewModel.recipes.isNotEmpty()) {
                    put(NavDisplay.TransitionKey) {
                        EnterTransition.None togetherWith slideLeftExit()
                    }
                    put(NavDisplay.PopTransitionKey) {
                        slideRightEnter() togetherWith ExitTransition.None
                    }
                }
            }
        ) {
            // On large screens, show the form and results side-by-side
            if (widthSizeClass == WindowWidthSizeClass.Compact) {
                FilterForm(searchViewModel) {
                    navigator.navigate(Routes.Results)
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
                        searchViewModel,
                        profileViewModel,
                        modifier = Modifier.weight(
                            if (widthSizeClass == WindowWidthSizeClass.Medium) 1f else 2f
                        )
                    ) { recipe ->
                        mainViewModel.recipe = recipe
                        navigator.navigate(Routes.Recipe(recipe.id))
                    }
                }
            }
        }
        entry<Routes.Results>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideLeftEnter() togetherWith slideLeftExit()
                }
                put(NavDisplay.PopTransitionKey) {
                    slideRightEnter() togetherWith slideRightExit()
                }
            }
        ) {
            SearchResults(searchViewModel, profileViewModel) { recipe ->
                mainViewModel.recipe = recipe
                navigator.navigate(Routes.Recipe(recipe.id))
            }
        }
        entry<Routes.Glossary> {
            Glossary(glossaryViewModel.terms)
        }
        entry<Routes.Profile>(
            metadata = metadata {
                if (mainViewModel.recipe != null) {
                    put(NavDisplay.TransitionKey) {
                        EnterTransition.None togetherWith slideLeftExit()
                    }
                    put(NavDisplay.PopTransitionKey) {
                        slideRightEnter() togetherWith ExitTransition.None
                    }
                }
            }
        ) { key ->
            Profile(
                profileViewModel,
                deepLinkAction = key.action
            )
        }
    }

    // Show the appropriate composable based on the current route, starting at the home screen
    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun NavigationGraphPreview() {
    val windowSize = currentWindowSize()
    val navigationState = rememberNavigationState()
    val navigator = remember { Navigator(navigationState) }

    CompositionLocalProvider(
        LocalNavigationState provides navigationState,
        LocalNavigator provides navigator
    ) {
        EZRecipesTheme {
            Surface {
                NavigationGraph(windowSize.widthSizeClass)
            }
        }
    }
}
