package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.recipe.RecipeRating
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.boldAnnotatedString
import kotlin.math.roundToInt

@Composable
fun RecipeCard(
    recipe: Recipe,
    width: Dp? = null,
    profileViewModel: ProfileViewModel,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val isFavorite = profileViewModel.chef?.favoriteRecipes?.contains(recipe.id.toString()) ?: false
    val calories = recipe.nutrients.firstOrNull { nutrient -> nutrient.name == "Calories" }

    LaunchedEffect(Unit) {
        profileViewModel.getChef()
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
            .then(if (width != null) Modifier.width(width) else Modifier)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(width = 312.dp, height = 231.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            HorizontalDivider()

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        profileViewModel.toggleFavoriteRecipe(recipe.id, !isFavorite)
                    },
                    enabled = profileViewModel.chef != null,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = if (isFavorite) {
                            stringResource(R.string.un_favorite_alt)
                        } else {
                            stringResource(R.string.favorite_alt)
                        }
                    )
                }
            }

            RecipeRating(
                averageRating = recipe.averageRating,
                totalRatings = recipe.totalRatings ?: 0,
                myRating = profileViewModel.chef?.ratings?.get(recipe.id.toString()),
                enabled = profileViewModel.chef != null,
                onRate = { rating ->
                    profileViewModel.rateRecipe(rating, recipe.id)
                }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = boldAnnotatedString(
                        text = context.resources.getQuantityString(R.plurals.recipe_time, recipe.time, recipe.time),
                        endIndex = 5 // "Time:".length = 5
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                calories?.let { calorie ->
                    Text(
                        text = "${calorie.amount.roundToInt()} ${calorie.unit}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipeCardPreview() {
    val context = LocalContext.current
    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )

    EZRecipesTheme {
        Surface {
            RecipeCard(
                recipe = Constants.Mocks.PINEAPPLE_SALAD,
                profileViewModel = profileViewModel
            ) {}
        }
    }
}
