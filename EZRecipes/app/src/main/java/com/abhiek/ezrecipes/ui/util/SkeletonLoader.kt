package com.abhiek.ezrecipes.ui.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

// Source: https://stackoverflow.com/a/78910106
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, delayMillis = 200),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )


    if (isVisible) {
        Box(modifier = modifier.background(brush)) {
            Box(modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = 1f })
        }
    } else {
        Box(modifier = modifier) {
            content()
        }

    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SkeletonLoaderPreview() {
    EZRecipesTheme {
        Surface {
            Row {
                SkeletonLoader(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(4.dp)
                        .weight(0.3f)
                        .clip(RoundedCornerShape(4.dp)),
                    isVisible = true
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = ""
                    )
                }
                SkeletonLoader(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(4.dp)
                        .weight(0.7f)
                        .clip(RoundedCornerShape(4.dp)),
                    isVisible = true
                ) {
                    Text(
                        text = "Content to display after content has loaded"
                    )
                }
            }
        }
    }
}
