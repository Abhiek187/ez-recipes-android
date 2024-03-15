package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import java.text.NumberFormat
import kotlin.math.roundToInt

@Composable
fun NutritionLabel(recipe: Recipe) {
    val context = LocalContext.current
    // Nutrients that should be bolded on the nutrition label
    val nutrientHeadings = listOf("Calories", "Fat", "Carbohydrates", "Protein")

    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(16.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Health score and servings
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.nutrition_facts),
                    style = MaterialTheme.typography.h5.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = stringResource(R.string.health_score, recipe.healthScore),
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = context.resources.getQuantityString(R.plurals.servings, recipe.servings, recipe.servings),
                    style = MaterialTheme.typography.subtitle1
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Nutritional information
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
            ) {
                recipe.nutrients.forEach { nutrient ->
                    val isBold = nutrientHeadings.contains(nutrient.name)
                    // Round each amount to a whole number and add commas
                    val formattedAmount = NumberFormat.getIntegerInstance().format(
                        nutrient.amount.roundToInt()
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = nutrient.name,
                            style = MaterialTheme.typography.body1.copy(
                                fontSize = 18.sp,
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Text(
                            text = "$formattedAmount ${nutrient.unit}",
                            style = MaterialTheme.typography.body1.copy(
                                fontSize = 18.sp,
                                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.End
                            )
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
fun NutritionLabelPreview() {
    EZRecipesTheme {
        Surface {
            NutritionLabel(MockRecipeService.recipes[1])
        }
    }
}
