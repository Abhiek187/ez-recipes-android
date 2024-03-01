package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.capitalizeWords

@Composable
fun RecipeTitle(recipe: Recipe) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Recipe name and source link
        Text(
            text = recipe.name.capitalizeWords(),
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            // Keep the recipe name and link next to each other
            // If the width is too small, wrap the text around the link
            modifier = Modifier.weight(1f, false)
        )

        IconButton(
            onClick = { uriHandler.openUri(recipe.url) }
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = stringResource(R.string.recipe_link),
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun RecipeTitlePreview() {
    EZRecipesTheme {
        Surface {
            RecipeTitle(MockRecipeService.recipes[1])
        }
    }
}
