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
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun HomePreview() {
    val viewModel = MainViewModel(RecipeRepository(MockRecipeService))

    EZRecipesTheme {
        Surface {
            Home(viewModel) {}
        }
    }
}
