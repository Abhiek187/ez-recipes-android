package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
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
    val resources = LocalResources.current
    // Nutrients that should be bolded on the nutrition label
    val nutrientHeadings = listOf("Calories", "Fat", "Carbohydrates", "Protein")

    ElevatedCard(
        modifier = Modifier
            .width(300.dp)
            .padding(16.dp)
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
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.health_score, recipe.healthScore),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = resources.getQuantityString(R.plurals.servings, recipe.servings, recipe.servings),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(
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
                            fontSize = 18.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "$formattedAmount ${nutrient.unit}",
                            fontSize = 18.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyLarge
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
