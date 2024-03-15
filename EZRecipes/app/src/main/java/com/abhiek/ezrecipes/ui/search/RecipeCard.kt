package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.boldAnnotatedString
import kotlin.math.roundToInt

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val calories = recipe.nutrients.firstOrNull { nutrient -> nutrient.name == "Calories" }

    Card(
        modifier = Modifier
            .widthIn(min = 350.dp, max = 450.dp)
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 2.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(width = 312.dp, height = 231.dp)
                    .padding(8.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit
            )
            Divider()

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { isFavorite = !isFavorite }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) stringResource(R.string.un_favorite_alt) else stringResource(
                            R.string.favorite_alt
                        ),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

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
                    style = MaterialTheme.typography.subtitle1
                )
                calories?.let { calorie ->
                    Text(
                        text = "${calorie.amount.roundToInt()} ${calorie.unit}",
                        style = MaterialTheme.typography.subtitle1
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
    EZRecipesTheme {
        Surface {
            RecipeCard(Constants.Mocks.PINEAPPLE_SALAD) {}
        }
    }
}
