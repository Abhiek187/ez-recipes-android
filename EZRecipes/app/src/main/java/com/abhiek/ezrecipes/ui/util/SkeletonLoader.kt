package com.abhiek.ezrecipes.ui.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.toPx

// Source: https://stackoverflow.com/a/78910106
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier
) {
    var width by remember { mutableFloatStateOf(0f) }

    val shimmerColors = listOf(
        Color.LightGray,
        Color.LightGray.copy(alpha = 0.7f),
        Color.LightGray
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = (width * 1.2).toPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = 0f)
    )

    Box(
        modifier = modifier
            .background(brush)
            .onGloballyPositioned { coordinates ->
                width = coordinates.size.width.toFloat()
            }
    ) {
        Box(modifier = Modifier
            .matchParentSize()
            .graphicsLayer { alpha = 1f })
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SkeletonLoaderPreview() {
    val loaderHeight = 20.dp

    EZRecipesTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                SkeletonLoader(
                    modifier = Modifier
                        .height(loaderHeight * 2)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonLoader(
                        modifier = Modifier
                            .height(loaderHeight)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    SkeletonLoader(
                        modifier = Modifier
                            .height(loaderHeight)
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonLoader(
                        modifier = Modifier
                            .height(loaderHeight)
                            .weight(0.3f)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    SkeletonLoader(
                        modifier = Modifier
                            .height(loaderHeight)
                            .weight(0.3f)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}
