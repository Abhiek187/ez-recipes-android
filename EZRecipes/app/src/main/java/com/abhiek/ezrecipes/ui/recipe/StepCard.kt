package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.models.Step
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepCard(step: Step) {
    ElevatedCard(
        modifier = Modifier
            .width(600.dp)
            .padding(8.dp)
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
                        .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                ) {
                    Text(
                        text = step.number.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Text(
                    text = step.step,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (step.ingredients.isNotEmpty()) {
                HorizontalDivider()

                // Ingredients for each step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.ingredients),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Wrap items if they can't fit in one row
                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        step.ingredients.forEach { ingredient ->
                            Column {
                                AsyncImage(
                                    model = stringResource(R.string.ingredient_url, ingredient.image),
                                    // Image alts aren't needed since there's text next to them
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                                Text(
                                    text = ingredient.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (step.equipment.isNotEmpty()) {
                HorizontalDivider()

                // Equipment for each step
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.equipment),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    FlowRow(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        step.equipment.forEach { equipment ->
                            Column {
                                AsyncImage(
                                    model = stringResource(R.string.equipment_url, equipment.image),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                                Text(
                                    text = equipment.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.width(100.dp)
                                )
                            }
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
            StepCard(MockRecipeService.recipes[1].instructions[0].steps[3])
        }
    }
}
