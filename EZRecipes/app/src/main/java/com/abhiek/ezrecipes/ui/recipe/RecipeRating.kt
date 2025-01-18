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
import androidx.compose.ui.platform.LocalContext
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
    totalRatings: Int?,
    myRating: Int? = null,
    onRate: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // If the user has rated the recipe, show their rating instead of the average
    val starRating = myRating?.toDouble() ?: averageRating
    val stars = starRating?.roundToInt() ?: 0
    val starColor = if (isSystemInDarkTheme()) {
        if (myRating != null) Orange700 else MaterialTheme.colorScheme.tertiary
    } else {
        if (myRating != null) Orange900 else Amber700
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clearAndSetSemantics {
            contentDescription = if (starRating == null) {
                context.getString(R.string.star_rating_none)
            } else {
                context.getString(
                    if (myRating != null) R.string.star_rating_user else R.string.star_rating_average,
                    starRating
                )
            }
            role = Role.Image
        }
    ) {
        if (starRating != null) {
            for (i in 1..5) {
                IconButton(
                    onClick = { onRate(i) }
                ) {
                    if (i < stars || (i == stars && starRating >= stars)) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = context.resources.getQuantityString(
                                R.plurals.star_rating_input, i, i
                            ),
                            tint = starColor
                        )
                    } else if (i == stars && starRating < stars) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.StarHalf,
                            contentDescription = context.resources.getQuantityString(
                                R.plurals.star_rating_input, i, i
                            ),
                            tint = starColor
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.StarRate,
                            contentDescription = context.resources.getQuantityString(
                                R.plurals.star_rating_input, i, i
                            ),
                            tint = starColor
                        )
                    }
                }
            }
        }
        Text(
            text = if (averageRating != null && totalRatings != null) {
                "(${averageRating.round(places = 1)}/5, " + context.resources.getQuantityString(
                    R.plurals.total_ratings, totalRatings, totalRatings.toShorthand()
                ) + ")"
            } else {
                "(0 ratings)"
            }
        )
    }
}

private data class RecipeRatingState(
    val averageRating: Double?,
    val totalRatings: Int?,
    val myRating: Int?
)

private class RecipeRatingPreviewParameterProvider: PreviewParameterProvider<RecipeRatingState> {
    override val values = sequenceOf(
        RecipeRatingState(averageRating = null, totalRatings = null, myRating = null),
        RecipeRatingState(averageRating = 5.0, totalRatings = 1, myRating = null),
        RecipeRatingState(averageRating = 4.1, totalRatings = 1934, myRating = null),
        RecipeRatingState(averageRating = 2.5, totalRatings = 10, myRating = null),
        RecipeRatingState(averageRating = 3.625, totalRatings = 582, myRating = null),
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
                    myRating = state.myRating
                )
            }
        }
    }
}
