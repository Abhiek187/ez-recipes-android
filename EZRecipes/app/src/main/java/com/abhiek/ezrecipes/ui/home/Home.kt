package com.abhiek.ezrecipes.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.abhiek.ezrecipes.R
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
import com.abhiek.ezrecipes.ui.search.RecipeCard
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.getActivity
import com.google.android.play.core.review.testing.FakeReviewManager
import kotlinx.coroutines.delay

@Composable
fun Home(
    mainViewModel: MainViewModel,
    onNavigateToRecipe: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.getActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val defaultLoadingMessage = ""
    var loadingMessage by remember { mutableStateOf(defaultLoadingMessage) }

    LaunchedEffect(Unit) {
        mainViewModel.fetchRecentRecipes()

        if (activity != null) {
            mainViewModel.presentReviewIfQualified(activity)
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
            if (mainViewModel.recipe != null) {
                onNavigateToRecipe()
                mainViewModel.isRecipeLoaded = false
            }
        }
    }

    // Listen to lifecycle changes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP &&
                context.getActivity()?.isChangingConfigurations != true) {
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
                backgroundColor = MaterialTheme.colors.secondary
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
            AlertDialog(
                onDismissRequest = {
                    mainViewModel.showRecipeAlert = false
                },
                title = {
                    Text(
                        text = stringResource(R.string.error_title)
                    )
                },
                text = {
                       Text(
                           text = mainViewModel.recipeError?.error ?:
                           stringResource(R.string.unknown_error)
                       )
                },
                buttons = {
                    // Position the button at the bottom right of the alert
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                mainViewModel.showRecipeAlert = false
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.ok_button)
                            )
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Recently viewed recipes
        // Smoothly fade in the recipe cards if they're slow to fetch from Room
        AnimatedVisibility(
            visible = mainViewModel.recentRecipes.isNotEmpty(),
            enter = fadeIn(
                tween(300, easing = LinearEasing)
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.recently_viewed),
                    style = MaterialTheme.typography.h4
                )
                Divider()

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
                            width = 350.dp
                        ) {
                            mainViewModel.recipe = recentRecipe.recipe
                            onNavigateToRecipe()
                        }
                    }
                }
            }
        }
    }
}

// Show different previews for each possible state of the home screen
private data class HomeState(
    val isLoading: Boolean,
    val showAlert: Boolean,
    val recentRecipes: List<Recipe>
)

private class HomePreviewParameterProvider: PreviewParameterProvider<HomeState> {
    // Show previews of the default home screen, with the progress bar, and with an alert
    override val values = sequenceOf(
        HomeState(isLoading = false, showAlert = false, recentRecipes = listOf(
            Constants.Mocks.PINEAPPLE_SALAD,
            Constants.Mocks.CHOCOLATE_CUPCAKE,
            Constants.Mocks.THAI_BASIL_CHICKEN
        )),
        HomeState(isLoading = false, showAlert = false, recentRecipes = listOf()),
        HomeState(isLoading = true, showAlert = false, recentRecipes = listOf()),
        HomeState(isLoading = false, showAlert = true, recentRecipes = listOf())
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

    val viewModel = MainViewModel(
        recipeRepository = RecipeRepository(recipeService, recentRecipeDao),
        dataStoreService = DataStoreService(context),
        reviewManager = FakeReviewManager(context)
    )
    val (isLoading, showAlert, recentRecipes) = state
    viewModel.isLoading = isLoading
    viewModel.showRecipeAlert = showAlert // show the fallback alert in the preview
    recipeService.isSuccess = !showAlert // show the alert after clicking the find recipe button
    viewModel.recentRecipes = recentRecipes.map { recipe ->
        RecentRecipe(recipe.id, System.currentTimeMillis(), recipe)
    }

    EZRecipesTheme {
        Surface {
            Home(viewModel) {}
        }
    }
}
