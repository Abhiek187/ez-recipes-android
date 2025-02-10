package com.abhiek.ezrecipes.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.RecentRecipe
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
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.profile.RecipeCardLoader
import com.abhiek.ezrecipes.ui.search.RecipeCard
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.Accordion
import com.abhiek.ezrecipes.ui.util.ErrorAlert
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.getActivity
import com.google.android.play.core.review.testing.FakeReviewManager
import kotlinx.coroutines.delay

@Composable
fun Home(
    mainViewModel: MainViewModel,
    profileViewModel: ProfileViewModel,
    expandAccordions: Boolean = false,
    onNavigateToRecipe: (recipe: Recipe) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.getActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val defaultLoadingMessage = ""
    var loadingMessage by remember { mutableStateOf(defaultLoadingMessage) }
    var didExpandFavorites by remember { mutableStateOf(false) }
    var didExpandRecent by remember { mutableStateOf(false) }
    var didExpandRates by remember { mutableStateOf(false) }

    val favoriteRecipes by profileViewModel.favoriteRecipes.collectAsState()
    val recentRecipes by profileViewModel.recentRecipes.collectAsState()
    val ratedRecipes by profileViewModel.ratedRecipes.collectAsState()

    val isLoggedIn = profileViewModel.authState == AuthState.AUTHENTICATED
    val isFetchingChef = profileViewModel.authState == AuthState.LOADING

    LaunchedEffect(Unit) {
        mainViewModel.fetchRecentRecipes()

        if (activity != null) {
            mainViewModel.presentReviewIfQualified(activity)
        }
        if (!isLoggedIn) {
            profileViewModel.getChef()
        }
    }

    LaunchedEffect(mainViewModel.isLoading) {
        // Don't show any messages initially if the recipe loads quickly
        loadingMessage = defaultLoadingMessage

        while (mainViewModel.isLoading) {
            delay(3000) // 3 seconds
            loadingMessage = context.resources.getStringArray(R.array.loading_messages).random()
        }
    }

    // Go to the recipe screen after fetching it from the server
    // Don't call this when navigating back
    if (mainViewModel.isRecipeLoaded) {
        LaunchedEffect(mainViewModel.recipe) {
            mainViewModel.recipe?.let { recipe ->
                onNavigateToRecipe(recipe)
                mainViewModel.isRecipeLoaded = false
            }
        }
    }

    // Listen to lifecycle changes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP &&
                activity?.isChangingConfigurations != true) {
                // Stop any network calls while switching tabs,
                // except when rotating or folding the screen
                mainViewModel.job?.cancel()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                for (recipe in recipes) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { mainViewModel.getRandomRecipe(fromHome = true) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            enabled = !mainViewModel.isLoading, // prevent button spam
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.find_recipe_button)
            )
        }

        // Show a progress bar while the recipe is loading
        // Make it hidden so the button stays in place
        CircularProgressIndicator(
            modifier = Modifier
                .alpha(if (mainViewModel.isLoading) 1f else 0f)
        )
        Text(
            text = loadingMessage,
            modifier = Modifier
                .alpha(if (mainViewModel.isLoading) 1f else 0f)
        )
        
        // Show an alert if the recipe failed to load
        if (mainViewModel.showRecipeAlert) {
            ErrorAlert(
                message = mainViewModel.recipeError?.error,
                onDismiss = {
                    mainViewModel.showRecipeAlert = false
                }
            )
        }

        // Saved recipes
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

// Show different previews for each possible state of the home screen
private data class HomeState(
    val isLoading: Boolean = false,
    val showAlert: Boolean = false,
    val recentRecipes: List<Recipe> = listOf(),
    val authState: AuthState = AuthState.UNAUTHENTICATED,
    // Expand the accordions by default to make it easier to distinguish each preview
    val expandAccordions: Boolean = true
)

private class HomePreviewParameterProvider: PreviewParameterProvider<HomeState> {
    // Show previews of the default home screen, with the progress bar, and with an alert
    override val values = sequenceOf(
        HomeState(recentRecipes = listOf(
            Constants.Mocks.PINEAPPLE_SALAD,
            Constants.Mocks.CHOCOLATE_CUPCAKE,
            Constants.Mocks.THAI_BASIL_CHICKEN
        )),
        HomeState(
            authState = AuthState.AUTHENTICATED,
            expandAccordions = false
        ),
        HomeState(authState = AuthState.LOADING),
        HomeState(),
        HomeState(isLoading = true),
        HomeState(showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun HomePreview(
    @PreviewParameter(HomePreviewParameterProvider::class) state: HomeState
) {
    val context = LocalContext.current
    val recipeService = MockRecipeService
    val recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()

    val mainViewModel = MainViewModel(
        recipeRepository = RecipeRepository(recipeService, recentRecipeDao),
        dataStoreService = DataStoreService(context),
        reviewManager = FakeReviewManager(context)
    )
    val (isLoading, showAlert, recentRecipes, authState, expandAccordions) = state
    mainViewModel.isLoading = isLoading
    mainViewModel.showRecipeAlert = showAlert // show the fallback alert in the preview
    recipeService.isSuccess = !showAlert // show the alert after clicking the find recipe button
    mainViewModel.recentRecipes = recentRecipes.map { recipe ->
        RecentRecipe(recipe.id, System.currentTimeMillis(), recipe)
    }

    val chefService = MockChefService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )
    profileViewModel.authState = authState
    if (authState == AuthState.AUTHENTICATED) {
        profileViewModel.chef = chefService.chef
    }

    EZRecipesTheme {
        Surface {
            Home(mainViewModel, profileViewModel, expandAccordions)
        }
    }
}
