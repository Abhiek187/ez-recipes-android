package com.abhiek.ezrecipes.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.PasskeyManager
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.search.RecipeCard
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.Accordion
import com.google.android.play.core.review.testing.FakeReviewManager

@Composable
fun HomeAccordions(
    mainViewModel: MainViewModel,
    profileViewModel: ProfileViewModel,
    expandAccordions: Boolean = false,
    onNavigateToRecipe: (recipe: Recipe) -> Unit = {}
) {
    var didExpandFavorites by remember { mutableStateOf(false) }
    var didExpandRecent by remember { mutableStateOf(false) }
    var didExpandRates by remember { mutableStateOf(false) }

    val favoriteRecipes by profileViewModel.favoriteRecipes.collectAsState()
    val recentRecipes by profileViewModel.recentRecipes.collectAsState()
    val ratedRecipes by profileViewModel.ratedRecipes.collectAsState()

    val isLoggedIn = profileViewModel.authState == AuthState.AUTHENTICATED
    val isFetchingChef = profileViewModel.authState == AuthState.LOADING

    @Composable
    fun loadRecipeCards(recipes: List<Recipe?>, showWhenOffline: Boolean = false) {
        if (!isLoggedIn) {
            if (showWhenOffline) {
                // Show what's stored on the device while the chef isn't signed in
                if (mainViewModel.recentRecipes.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_results),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        mainViewModel.recentRecipes.forEach { recentRecipe ->
                            key(recentRecipe.id) {
                                RecipeCard(
                                    recipe = recentRecipe.recipe,
                                    width = 350.dp,
                                    profileViewModel = profileViewModel
                                ) {
                                    onNavigateToRecipe(recentRecipe.recipe)
                                }
                            }
                        }
                    }
                }
            } else if (isFetchingChef) {
                // Show the recipe cards loading while waiting for both the auth state & recipes
                RecipeCardLoader()
            } else {
                // Encourage the user to sign in to see these recipes
                Text(
                    text = stringResource(R.string.sign_in_for_recipes),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        } else if (recipes.isEmpty()) {
            Text(
                text = stringResource(R.string.no_results),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                itemsIndexed(
                    items = recipes,
                    key = { index, recipe -> recipe?.id ?: index }
                ) { _, recipe ->
                    if (recipe == null) {
                        RecipeCardLoader()
                    } else {
                        RecipeCard(
                            recipe = recipe,
                            width = 350.dp,
                            profileViewModel = profileViewModel
                        ) {
                            onNavigateToRecipe(recipe)
                        }
                    }
                }
            }
        }
    }

    Column {
        Accordion(
            header = stringResource(R.string.profile_favorites),
            expandByDefault = expandAccordions,
            onExpand = {
                // Only fetch the recipes once per load
                if (isLoggedIn && !didExpandFavorites) {
                    profileViewModel.getAllFavoriteRecipes()
                    didExpandFavorites = true
                }
            }
        ) {
            loadRecipeCards(favoriteRecipes)
        }
        Accordion(
            header = stringResource(R.string.profile_recently_viewed),
            expandByDefault = expandAccordions,
            onExpand = {
                if (isLoggedIn && !didExpandRecent) {
                    profileViewModel.getAllRecentRecipes()
                    didExpandRecent = true
                }
            }
        ) {
            loadRecipeCards(recentRecipes, showWhenOffline = true)
        }
        Accordion(
            header = stringResource(R.string.profile_ratings),
            expandByDefault = expandAccordions,
            onExpand = {
                if (isLoggedIn && !didExpandRates) {
                    profileViewModel.getAllRatedRecipes()
                    didExpandRates = true
                }
            }
        ) {
            loadRecipeCards(ratedRecipes)
        }
    }
}

private class HomeAccordionsPreviewParameterProvider :
    PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun HomeAccordionsPreview(
    @PreviewParameter(HomeAccordionsPreviewParameterProvider::class) expandAccordions: Boolean
) {
    val context = LocalContext.current
    val recipeService = MockRecipeService
    val recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()

    val mainViewModel = viewModel {
        MainViewModel(
            recipeRepository = RecipeRepository(recipeService, recentRecipeDao),
            dataStoreService = DataStoreService(context),
            reviewManager = FakeReviewManager(context)
        )
    }

    val chefService = MockChefService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context),
            passkeyManager = PasskeyManager(context)
        )
    }

    EZRecipesTheme {
        Surface {
            HomeAccordions(mainViewModel, profileViewModel, expandAccordions)
        }
    }
}
