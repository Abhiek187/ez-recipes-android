package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.models.Step
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun StepCard(step: Step) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 10.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            // Step number and instruction
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    // Center text vertically
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .border(1.dp, MaterialTheme.colors.onBackground, CircleShape)
                ) {
                    Text(
                        text = step.number.toString(),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = step.step
                )
            }

            if (step.ingredients.isNotEmpty()) {
                Divider()

                // Ingredients for each step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.ingredients),
                        fontWeight = FontWeight.Bold
                    )

                    step.ingredients.forEach { ingredient ->
                        Column {
                            AsyncImage(
                                model = stringResource(R.string.ingredient_url, ingredient.image),
                                contentDescription = ingredient.name,
                                modifier = Modifier.size(100.dp)
                            )
                            Text(
                                text = ingredient.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }

            if (step.equipment.isNotEmpty()) {
                Divider()

                // Equipment for each step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.equipment),
                        fontWeight = FontWeight.Bold
                    )

                    step.equipment.forEach { equipment ->
                        Column {
                            AsyncImage(
                                model = stringResource(R.string.equipment_url, equipment.image),
                                contentDescription = equipment.name,
                                modifier = Modifier.size(100.dp)
                            )
                            Text(
                                text = equipment.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(100.dp)
                            )
                        }
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
fun StepCardPreview() {
    EZRecipesTheme {
        Surface {
            StepCard(MockRecipeService.recipe.instructions[0].steps[0])
        }
    }
}
