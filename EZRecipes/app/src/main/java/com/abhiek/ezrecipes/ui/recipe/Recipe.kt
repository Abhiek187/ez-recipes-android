package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun Recipe(recipe: Recipe) {
    println(recipe)
    Text(
        text = recipe.name,
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 8.dp)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun RecipePreview() {
    EZRecipesTheme {
        Surface {
            Recipe(MockRecipeService.recipe)
        }
    }
}
