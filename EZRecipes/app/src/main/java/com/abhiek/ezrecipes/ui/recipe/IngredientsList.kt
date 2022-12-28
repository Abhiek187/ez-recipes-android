package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.capitalizeWords

@Composable
fun IngredientsList(recipe: Recipe) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.ingredients),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Divider()

            // Ingredients list
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                recipe.ingredients.forEach { ingredient ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${ingredient.amount} ${ingredient.unit}",
                            fontSize = 18.sp
                        )
                        Text(
                            text = ingredient.name.capitalizeWords(),
                            fontSize = 18.sp
                        )
                    }
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
fun IngredientsListPreview() {
    EZRecipesTheme {
        Surface {
            IngredientsList(MockRecipeService.recipe)
        }
    }
}
