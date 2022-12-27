package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.Blue300
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun RecipeHeader(recipe: Recipe) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val annotationTag = "URL"

    // Make the image caption clickable
    val annotatedLinkString = buildAnnotatedString {
        val str = stringResource(R.string.image_copyright, recipe.credit ?: "")
        val startIndex = 8 // "Image © ".length = 8
        val endIndex = str.length // exclusive
        append(str)

        // Apply a blue link style to the section after the copyright symbol
        addStyle(
            style = SpanStyle(
                color = Blue300,
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline
            ),
            start = startIndex,
            end = endIndex
        )

        // Point the link to the source URL and tag it for reference
        addStringAnnotation(
            tag = annotationTag,
            annotation = recipe.sourceUrl,
            start = startIndex,
            end = endIndex
        )
    }

    val timeAnnotatedString = buildAnnotatedString {
        val str = context.resources.getQuantityString(R.plurals.recipe_time, recipe.time, recipe.time)
        val startIndex = 0
        val endIndex = 5 // "Time:".length = 5
        append(str)

        // Bold only the time portion of the string
        addStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold
            ),
            start = startIndex,
            end = endIndex
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recipe name and source link
            Text(
                text = recipe.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            IconButton(
                onClick = { uriHandler.openUri(recipe.url) }
            ) {
                Icon(Icons.Default.Link, stringResource(R.string.recipe_link))
            }
        }

        // Recipe image and caption
        AsyncImage(
            model = recipe.image,
            contentDescription = recipe.name,
            modifier = Modifier.size(300.dp)
        )

        if (recipe.credit != null) {
            ClickableText(
                text = annotatedLinkString,
                style = TextStyle(
                    color = MaterialTheme.colors.onBackground
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) { offset ->
                // Open the URL from the annotated string
                annotatedLinkString
                    .getStringAnnotations(annotationTag, offset, offset)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        }

        // Recipe time and buttons
        Text(
            text = timeAnnotatedString,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.size(50.dp), // avoid the oval shape
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    ),
                    onClick = { println("Nice! Hope it was tasty!") }
                ) {
                    Icon(Icons.Default.Restaurant, stringResource(R.string.made_button))
                }
                Text(
                    text = stringResource(R.string.made_button),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary
                    ),
                    onClick = { println("Showing another recipe...") }
                ) {
                    Icon(Icons.Default.ReceiptLong, stringResource(R.string.show_recipe_button))
                }
                Text(
                    text = stringResource(R.string.show_recipe_button),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun RecipeHeaderPreview() {
    EZRecipesTheme {
        Surface {
            RecipeHeader(MockRecipeService.recipe)
        }
    }
}
