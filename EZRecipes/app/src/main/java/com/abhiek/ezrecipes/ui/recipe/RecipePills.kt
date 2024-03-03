package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.SpiceLevel
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.Amber700
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.capitalizeWords

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipePills(
    spiceLevel: SpiceLevel,
    isVegetarian: Boolean,
    isVegan: Boolean,
    isGlutenFree: Boolean,
    isHealthy: Boolean,
    isCheap: Boolean,
    isSustainable: Boolean
) {
    FlowRow(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (spiceLevel == SpiceLevel.MILD || spiceLevel == SpiceLevel.SPICY) {
            Pill(
                text = spiceLevel.toString().capitalizeWords(),
                backgroundColor = if (spiceLevel == SpiceLevel.MILD) Amber700 else Color.Red
            )
        }
        if (isVegetarian) {
            Pill(
                text = stringResource(R.string.vegetarian_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
        if (isVegan) {
            Pill(
                text = stringResource(R.string.vegan_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
        if (isGlutenFree) {
            Pill(
                text = stringResource(R.string.gluten_free_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
        if (isHealthy) {
            Pill(
                text = stringResource(R.string.healthy_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
        if (isCheap) {
            Pill(
                text = stringResource(R.string.cheap_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
        if (isSustainable) {
            Pill(
                text = stringResource(R.string.sustainable_label),
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun Pill(text: String, backgroundColor: Color, textColor: Color = Color.Black) {
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(corner = CornerSize(50)))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class RecipePillsState(
    val spiceLevel: SpiceLevel = SpiceLevel.NONE,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val isGlutenFree: Boolean = false,
    val isHealthy: Boolean = false,
    val isCheap: Boolean = false,
    val isSustainable: Boolean = false
)

private class RecipePillsPreviewParameterProvider: PreviewParameterProvider<RecipePillsState> {
    override val values = sequenceOf(
        RecipePillsState(isVegetarian = true, isVegan = true),
        RecipePillsState(spiceLevel = SpiceLevel.MILD, isGlutenFree = true, isHealthy = true),
        RecipePillsState(
            spiceLevel = SpiceLevel.SPICY,
            isVegetarian = true,
            isVegan = true,
            isGlutenFree = true,
            isHealthy = true,
            isCheap = true,
            isSustainable = true
        )
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipePillsPreview(
    @PreviewParameter(RecipePillsPreviewParameterProvider::class) state: RecipePillsState
) {
    EZRecipesTheme {
        Surface {
            RecipePills(
                state.spiceLevel,
                state.isVegetarian,
                state.isVegan,
                state.isGlutenFree,
                state.isHealthy,
                state.isCheap,
                state.isSustainable
            )
        }
    }
}
