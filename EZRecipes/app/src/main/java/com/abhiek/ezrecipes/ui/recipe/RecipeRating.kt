package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.Amber700
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.round
import com.abhiek.ezrecipes.utils.toShorthand
import kotlin.math.roundToInt

@Composable
fun RecipeRating(averageRating: Double?, totalRatings: Int?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val stars = averageRating?.roundToInt() ?: 0
    val starColor = if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.tertiary
    } else {
        Amber700
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (averageRating != null) {
            for (i in 1..5) {
                if (i < stars || (i == stars && averageRating >= stars)) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.star_filled),
                        tint = starColor
                    )
                } else if (i == stars && averageRating < stars) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = stringResource(R.string.star_half),
                        tint = starColor
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.StarRate,
                        contentDescription = stringResource(R.string.star_empty),
                        tint = starColor
                    )
                }
            }
        }
        Text(
            text = if (averageRating != null && totalRatings != null) {
                "(${averageRating.round(places = 1)}/5, " + context.resources.getQuantityString(
                    R.plurals.ratings, totalRatings, totalRatings.toShorthand()
                ) + ")"
            } else {
                "(0 ratings)"
            }
        )
    }
}

private data class RecipeRatingState(
    val averageRating: Double?,
    val totalRatings: Int?
)

private class RecipeRatingPreviewParameterProvider: PreviewParameterProvider<RecipeRatingState> {
    override val values = sequenceOf(
        RecipeRatingState(averageRating = null, totalRatings = null),
        RecipeRatingState(averageRating = 5.0, totalRatings = 1),
        RecipeRatingState(averageRating = 4.1, totalRatings = 1934),
        RecipeRatingState(averageRating = 2.5, totalRatings = 10),
        RecipeRatingState(averageRating = 3.625, totalRatings = 582)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipeRatingPreview(
    @PreviewParameter(RecipeRatingPreviewParameterProvider::class) state: RecipeRatingState
) {
    EZRecipesTheme {
        Surface {
            // The status bar and camera get in the way
            Column {
                Spacer(modifier = Modifier.height(50.dp))
                RecipeRating(
                    averageRating = state.averageRating,
                    totalRatings = state.totalRatings
                )
            }
        }
    }
}
