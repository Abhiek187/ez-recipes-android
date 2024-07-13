package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
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
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.google.android.play.core.review.testing.FakeReviewManager

@Composable
fun SearchResults(
    mainViewModel: MainViewModel,
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier,
    onNavigateToRecipe: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.results_title),
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Should only be visible on large screens
        if (searchViewModel.recipes.isEmpty()) {
            // Center vertically & horizontally
            Text(
                text = stringResource(R.string.results_placeholder),
                style = MaterialTheme.typography.h6.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(Alignment.CenterVertically)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 350.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f), // occupy remaining space when loader isn't visible
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchViewModel.recipes) { recipe ->
                    RecipeCard(recipe) {
                        mainViewModel.recipe = recipe
                        onNavigateToRecipe()
                    }
                }
                // Invisible detector when the user scrolls to the bottom of the list
                // https://stackoverflow.com/a/71875618
                item {
                    LaunchedEffect(true) {
                        // Prevent multiple requests from running at once
                        if (searchViewModel.lastToken != null && !searchViewModel.isLoading) {
                            searchViewModel.searchRecipes(paginate = true)
                        }
                    }
                }
            }

            if (searchViewModel.isLoading && searchViewModel.lastToken != null) {
                CircularProgressIndicator()
            }
        }
    }
}

private class SearchResultsPreviewParameterProvider: PreviewParameterProvider<List<Recipe>> {
    override val values = sequenceOf(
        listOf(),
        listOf(
            Constants.Mocks.PINEAPPLE_SALAD,
            Constants.Mocks.CHOCOLATE_CUPCAKE,
            Constants.Mocks.THAI_BASIL_CHICKEN
        )
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SearchResultsPreview(
    @PreviewParameter(SearchResultsPreviewParameterProvider::class) recipes: List<Recipe>
) {
    val context = LocalContext.current
    val recipeService = MockRecipeService
    val recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()

    val recipeViewModel = MainViewModel(
        recipeRepository = RecipeRepository(recipeService, recentRecipeDao),
        dataStoreService = DataStoreService(context),
        reviewManager = FakeReviewManager(context)
    )
    val searchViewModel = SearchViewModel(RecipeRepository((recipeService)))
    searchViewModel.recipes = recipes

    EZRecipesTheme {
        Surface {
            SearchResults(recipeViewModel, searchViewModel) {}
        }
    }
}
