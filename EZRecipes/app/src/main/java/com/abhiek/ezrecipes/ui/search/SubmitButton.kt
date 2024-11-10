package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.abhiek.ezrecipes.ui.home.ErrorAlert
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import kotlinx.coroutines.delay

@Composable
fun SubmitButton(searchViewModel: SearchViewModel, enabled: Boolean) {
    val defaultLoadingMessage = ""
    var loadingMessage by remember { mutableStateOf(defaultLoadingMessage) }

    val context = LocalContext.current

    LaunchedEffect(searchViewModel.isLoading) {
        // Don't show any messages initially if the recipe loads quickly
        loadingMessage = defaultLoadingMessage

        while (searchViewModel.isLoading) {
            delay(3000) // 3 seconds
            loadingMessage = context.resources.getStringArray(R.array.loading_messages).random()
        }
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Button(
                onClick = { searchViewModel.searchRecipes() },
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.submit_button))
            }
            CircularProgressIndicator(
                modifier = Modifier
                    .alpha(if (searchViewModel.isLoading) 1f else 0f)
            )
        }

        if (searchViewModel.isLoading) {
            Text(
                text = loadingMessage,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Show an alert if the recipe failed to load
        if (searchViewModel.showRecipeAlert) {
            ErrorAlert(
                message = searchViewModel.recipeError?.error,
                onDismiss = {
                    searchViewModel.showRecipeAlert = false
                }
            )
        }
    }
}

private data class SubmitButtonState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class SubmitButtonPreviewParameterProvider: PreviewParameterProvider<SubmitButtonState> {
    override val values = sequenceOf(
        SubmitButtonState(isLoading = false, showAlert = false),
        SubmitButtonState(isLoading = true, showAlert = false),
        SubmitButtonState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SubmitButtonPreview(
    @PreviewParameter(SubmitButtonPreviewParameterProvider::class) state: SubmitButtonState
) {
    val recipeService = MockRecipeService
    val viewModel = SearchViewModel(RecipeRepository(recipeService))
    val (isLoading, showAlert) = state
    viewModel.isLoading = isLoading
    viewModel.showRecipeAlert = showAlert
    recipeService.isSuccess = !showAlert

    EZRecipesTheme {
        Surface {
            SubmitButton(viewModel, !viewModel.isLoading)
        }
    }
}
