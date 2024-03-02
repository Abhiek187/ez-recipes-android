package com.abhiek.ezrecipes.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import kotlinx.coroutines.delay

@Composable
fun Home(
    mainViewModel: MainViewModel,
    onNavigateToRecipe: () -> Unit
) {
    val context = LocalContext.current
    val defaultLoadingMessage = ""
    var loadingMessage by remember { mutableStateOf(defaultLoadingMessage) }

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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { mainViewModel.getRandomRecipe(fromHome = true) },
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
    viewModel.showRecipeAlert = showAlert // show the fallback alert in the preview
    recipeService.isSuccess = !showAlert // show the alert after clicking the find recipe button

    EZRecipesTheme {
        Surface {
            Home(viewModel) {}
        }
    }
}
