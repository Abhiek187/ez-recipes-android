package com.abhiek.ezrecipes.ui.search

import androidx.compose.animation.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun FormError(
    on: Boolean,
    message: String,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = on,
        enter = slideInVertically {
            // Slide in from 40 dp from the top
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = style
        )
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun FormErrorPreview() {
    EZRecipesTheme {
        Surface {
            FormError(on = false, message = "You can't see me")
            FormError(on = true, message = "Preview error (jk)")
            FormError(
                on = true,
                message = "Larger error",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}
