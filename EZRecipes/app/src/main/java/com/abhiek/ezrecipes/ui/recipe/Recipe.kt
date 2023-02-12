package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.RecipeRepository
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun Recipe(viewModel: MainViewModel, isWideScreen: Boolean, recipeIdString: String? = null) {
    if (viewModel.recipe == null) {
        // If this composable was opened due to a deep link, use the recipeId to load the recipe
        recipeIdString?.toIntOrNull()?.let { recipeId ->
            viewModel.getRecipeById(recipeId)
        }

        // Shouldn't be seen normally
        Column {
            Text(
                text = stringResource(R.string.no_recipe),
                modifier = Modifier.padding(8.dp)
            )

            if (viewModel.isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    viewModel.recipe?.let { recipe ->
        // Make the column scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecipeTitle(recipe = recipe)

            // Show the recipe components side-by-side if there's enough screen space
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RecipeHeader(
                        recipe = recipe,
                        isLoading = viewModel.isLoading
                    ) {
                        // Load another recipe in the same view
                        viewModel.getRandomRecipe()
                    }
                    NutritionLabel(recipe = recipe)
                }
            } else {
                RecipeHeader(
                    recipe = recipe,
                    isLoading = viewModel.isLoading
                ) {
                    viewModel.getRandomRecipe()
                }
                NutritionLabel(recipe = recipe)
            }

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SummaryBox(summary = recipe.summary)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IngredientsList(ingredients = recipe.ingredients)
                    }
                }
            } else {
                SummaryBox(summary = recipe.summary)
                IngredientsList(ingredients = recipe.ingredients)
            }

            InstructionsList(instructions = recipe.instructions)

            Divider()

            RecipeFooter()
        }
    }
}

private class RecipePreviewParameterProvider: PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipePreview(
    @PreviewParameter(RecipePreviewParameterProvider::class) isWideScreen: Boolean
) {
    val viewModel = MainViewModel(RecipeRepository(MockRecipeService))
    viewModel.getRandomRecipe()

    EZRecipesTheme {
        Surface {
            // Copy the Recipe composable so the ViewModel loads in the preview
            Recipe(viewModel, isWideScreen)
        }
    }
}
