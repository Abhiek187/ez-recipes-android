package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
fun Recipe(viewModel: MainViewModel) {
    if (viewModel.recipe != null) {
        val recipe = viewModel.recipe!!

        // Make the column scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecipeHeader(
                recipe = recipe,
                isLoading = viewModel.isLoading
            ) {
                // Load another recipe in the same view
                viewModel.getRandomRecipe()
            }
            NutritionLabel(recipe = recipe)
            SummaryBox(summary = recipe.summary)
            IngredientsList(ingredients = recipe.ingredients)
            InstructionsList(instructions = recipe.instructions)

            Divider()

            RecipeFooter()
        }
    } else {
        // Shouldn't be seen normally
        Text(
            text = stringResource(R.string.no_recipe),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun RecipePreview() {
    val viewModel = MainViewModel(RecipeRepository(MockRecipeService))

    EZRecipesTheme {
        Surface {
            Recipe(viewModel)
        }
    }
}
