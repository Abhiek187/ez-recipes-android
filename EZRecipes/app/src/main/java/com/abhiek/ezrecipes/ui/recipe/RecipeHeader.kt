package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.Cuisine
import com.abhiek.ezrecipes.data.models.MealType
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.boldAnnotatedString
import com.abhiek.ezrecipes.utils.contentEquals
import com.abhiek.ezrecipes.utils.toShorthand

@Composable
fun RecipeHeader(
    recipe: Recipe,
    isLoading: Boolean,
    myRating: Int? = null,
    onRate: (Int) -> Unit = {},
    onClickFindRecipe: () -> Unit = {}
) {
    val context = LocalContext.current

    // Make the image caption clickable
    val annotatedLinkString = buildAnnotatedString {
        val str = stringResource(R.string.image_copyright, recipe.credit)
        val startIndex = 8 // "Image © ".length = 8
        val endIndex = str.length // exclusive
        append(str.substring(0, startIndex))

        // Apply a blue link style to the section after the copyright symbol
        withLink(
            LinkAnnotation.Url(
                url = recipe.sourceUrl,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                )
            )
        ) {
            append(str.substring(startIndex, endIndex))
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Allow content to take up the full width if needed
        modifier = Modifier.width(IntrinsicSize.Max)
    ) {
        // Recipe image and caption
        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.name,
            modifier = Modifier
                .size(width = 312.dp, height = 231.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = annotatedLinkString,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Recipe info
        RecipePills(
            spiceLevel = recipe.spiceLevel,
            isVegetarian = recipe.isVegetarian,
            isVegan = recipe.isVegan,
            isGlutenFree = recipe.isGlutenFree,
            isHealthy = recipe.isHealthy,
            isCheap = recipe.isCheap,
            isSustainable = recipe.isSustainable
        )

        // Recipe time, views, and buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = boldAnnotatedString(
                    // 2nd arg = count, 3rd arg = formatter args
                    text = context.resources.getQuantityString(R.plurals.recipe_time, recipe.time, recipe.time),
                    endIndex = 5 // "Time:".length = 5
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = stringResource(R.string.views_alt)
            )
            Text(
                text = recipe.views?.toShorthand() ?: "0"
            )
        }
        RecipeRating(
            averageRating = recipe.averageRating,
            totalRatings = recipe.totalRatings ?: 0,
            myRating = myRating,
            onRate = onRate,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (recipe.types.isNotEmpty() && !(recipe.types contentEquals listOf(MealType.UNKNOWN))) {
            Text(
                text = boldAnnotatedString(
                    text = stringResource(
                        R.string.recipe_meal_types,
                        recipe.types.filter { it != MealType.UNKNOWN }.joinToString(", ")
                    ),
                    endIndex = 10 // "Great for:".length = 10
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
        if (recipe.culture.isNotEmpty() && !(recipe.culture contentEquals listOf(Cuisine.UNKNOWN))) {
            Text(
                text = boldAnnotatedString(
                    text = stringResource(
                        R.string.recipe_cuisines,
                        recipe.culture.filter { it != Cuisine.UNKNOWN }.joinToString(", ")
                    ),
                    endIndex = 9 // "Cuisines:".length = 9
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.size(50.dp), // avoid the oval shape
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    onClick = { onClickFindRecipe() },
                    // Reduce padding to make the icon take up more space
                    contentPadding = PaddingValues(8.dp),
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = stringResource(R.string.show_recipe_button)
                    )
                }
                Text(
                    text = stringResource(R.string.show_recipe_button),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}

private class RecipeHeaderPreviewParameterProvider: PreviewParameterProvider<Boolean> {
    // Show a preview when the view is loading and not loading
    override val values = sequenceOf(true, false)
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipeHeaderPreview(
    @PreviewParameter(RecipeHeaderPreviewParameterProvider::class) isLoading: Boolean
) {
    EZRecipesTheme {
        Surface {
            RecipeHeader(
                recipe = MockRecipeService.recipes[2],
                isLoading = isLoading,
                myRating = 4
            )
        }
    }
}
