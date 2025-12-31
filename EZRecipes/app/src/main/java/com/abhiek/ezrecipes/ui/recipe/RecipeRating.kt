package com.abhiek.ezrecipes.ui.recipe

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
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
import com.abhiek.ezrecipes.ui.theme.Orange700
import com.abhiek.ezrecipes.ui.theme.Orange900
import com.abhiek.ezrecipes.utils.round
import com.abhiek.ezrecipes.utils.toShorthand
import kotlin.math.roundToInt

@Composable
fun RecipeRating(
    averageRating: Double?,
    totalRatings: Int,
    modifier: Modifier = Modifier,
    myRating: Int? = null,
    enabled: Boolean = true,
    onRate: (Int) -> Unit = {}
) {
    val resources = LocalResources.current

    // If the user has rated the recipe, show their rating instead of the average
    // If there are no ratings, show all empty stars
    val starRating = myRating?.toDouble() ?: averageRating ?: 0.0
    val stars = starRating.roundToInt()
    val starColor = if (isSystemInDarkTheme()) {
        if (myRating != null) Orange700 else MaterialTheme.colorScheme.tertiary
    } else {
        if (myRating != null) Orange900 else Amber700
    }

    FlowRow(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = if (starRating == 0.0) {
                resources.getString(R.string.star_rating_none)
            } else if (myRating != null) {
                resources.getString(R.string.star_rating_user, myRating)
            } else {
                resources.getString(R.string.star_rating_average, starRating)
            }
            role = Role.Image
        }
    ) {
        for (i in 1..5) {
            IconButton(
                onClick = { onRate(i) },
                enabled = enabled,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = starColor
                )
            ) {
                if (i < stars || (i == stars && starRating >= stars)) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = resources.getQuantityString(
                            R.plurals.star_rating_input, i, i
                        )
                    )
                } else if (i == stars && starRating < stars) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = resources.getQuantityString(
                            R.plurals.star_rating_input, i, i
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.StarRate,
                        contentDescription = resources.getQuantityString(
                            R.plurals.star_rating_input, i, i
                        )
                    )
                }
            }
        }
        Text(
            text = "(" + (if (averageRating != null) {
                "${averageRating.round(places = 1)}/5, "
            } else "") + resources.getQuantityString(
                R.plurals.total_ratings, totalRatings, totalRatings.toShorthand()
            ) + ")",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f, false)
        )
    }
}

private data class RecipeRatingState(
    val averageRating: Double? = null,
    val totalRatings: Int = 0,
    val myRating: Int? = null,
    val enabled: Boolean = true
)

private class RecipeRatingPreviewParameterProvider: PreviewParameterProvider<RecipeRatingState> {
    override val values = sequenceOf(
        RecipeRatingState(),
        RecipeRatingState(enabled = false),
        RecipeRatingState(averageRating = 5.0, totalRatings = 1),
        RecipeRatingState(averageRating = 4.1, totalRatings = 1934),
        RecipeRatingState(averageRating = 2.5, totalRatings = 10),
        RecipeRatingState(averageRating = 3.625, totalRatings = 582),
        RecipeRatingState(averageRating = 1.0, totalRatings = 8, myRating = 3)
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
                    totalRatings = state.totalRatings,
                    myRating = state.myRating,
                    enabled = state.enabled
                )
            }
        }
    }
}
