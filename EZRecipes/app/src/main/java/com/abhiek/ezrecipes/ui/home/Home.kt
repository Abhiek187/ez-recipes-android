package com.abhiek.ezrecipes.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.RecipeRepository
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.MainViewModelFactory
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun Home(
    mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory()
    ),
    onNavigateToRecipe: (recipe: Recipe) -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { mainViewModel.getRandomRecipe() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            enabled = !mainViewModel.isLoading // prevent button spam
        ) {
            Text(
                text = stringResource(R.string.find_recipe_button)
            )
        }

        // Show a progress bar while the recipe is loading
        if (mainViewModel.isLoading) {
            CircularProgressIndicator()
        }
        
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
            )
        }
    }
}

// Show different previews for each possible state of the home screen
private data class HomeState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class HomePreviewParameterProvider: PreviewParameterProvider<HomeState> {
    // Show previews of the default home screen, with the progress bar, and with an alert
    override val values = sequenceOf(
        HomeState(isLoading = false, showAlert = false),
        HomeState(isLoading = true, showAlert = false),
        HomeState(isLoading = false, showAlert = true)
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
    val recipeService = MockRecipeService
    val viewModel = MainViewModel(RecipeRepository(recipeService))
    val (isLoading, showAlert) = state
    viewModel.isLoading = isLoading
    //viewModel.showRecipeAlert = showAlert

    if (showAlert) {
        recipeService.isSuccess = false
    }

    EZRecipesTheme {
        Surface {
            Home(viewModel) {}
        }
    }
}
