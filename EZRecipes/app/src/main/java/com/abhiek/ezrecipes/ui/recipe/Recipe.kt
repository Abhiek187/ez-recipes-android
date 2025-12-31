package com.abhiek.ezrecipes.ui.recipe

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
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
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.currentWindowSize
import com.google.android.play.core.review.testing.FakeReviewManager

@Composable
fun Recipe(
    mainViewModel: MainViewModel,
    profileViewModel: ProfileViewModel,
    isWideScreen: Boolean,
    recipeIdString: String? = null
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    fun rateRecipe(rating: Int, recipeId: Int) {
        if (profileViewModel.chef == null) {
            Toast.makeText(
                context,
                resources.getString(R.string.rating_error),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            profileViewModel.rateRecipe(recipeId, rating)
        }
    }

    LaunchedEffect(mainViewModel.recipe) {
        mainViewModel.recipe?.let { recipe ->
            // If logged in, save recipe to chef's profile. Otherwise, save to temporary storage.
            mainViewModel.saveRecentRecipe(recipe)
            profileViewModel.updateRecipeViews(recipe)
        }
    }

    if (mainViewModel.recipe == null) {
        // If this composable was opened due to a deep link, use the recipeId to load the recipe
        recipeIdString?.toIntOrNull()?.let { recipeId ->
            mainViewModel.getRecipeById(recipeId)
        }

        // Shouldn't be seen normally
        Column {
            Text(
                text = stringResource(R.string.no_recipe),
                modifier = Modifier.padding(8.dp)
            )

            if (mainViewModel.isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    mainViewModel.recipe?.let { recipe ->
        val chefRating = profileViewModel.chef?.ratings?.get(recipe.id.toString())

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
                        isLoading = mainViewModel.isLoading,
                        myRating = chefRating,
                        onRate = { rating ->
                            rateRecipe(rating, recipe.id)
                        },
                        onClickFindRecipe = {
                            // Load another recipe in the same view
                            mainViewModel.getRandomRecipe()
                        }
                    )
                    NutritionLabel(recipe = recipe)
                }
            } else {
                RecipeHeader(
                    recipe = recipe,
                    isLoading = mainViewModel.isLoading,
                    myRating = chefRating,
                    onRate = { rating ->
                        rateRecipe(rating, recipe.id)
                    },
                    onClickFindRecipe = {
                        mainViewModel.getRandomRecipe()
                    }
                )
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

            HorizontalDivider()

            RecipeFooter()
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipePreview() {
    val context = LocalContext.current
    val recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()
    val recipeService = MockRecipeService

    val viewModel = viewModel {
        MainViewModel(
            recipeRepository = RecipeRepository(recipeService, recentRecipeDao),
            dataStoreService = DataStoreService(context),
            reviewManager = FakeReviewManager(context)
        )
    }
    viewModel.getRandomRecipe()
    val windowSize = currentWindowSize()

    val chefService = MockChefService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context)
        )
    }

    EZRecipesTheme {
        Surface {
            Recipe(
                viewModel,
                profileViewModel,
                windowSize.widthSizeClass == WindowWidthSizeClass.Expanded
            )
        }
    }
}
