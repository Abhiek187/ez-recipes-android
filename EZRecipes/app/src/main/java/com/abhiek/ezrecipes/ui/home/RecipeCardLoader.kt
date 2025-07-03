package com.abhiek.ezrecipes.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.SkeletonLoader

@Composable
fun RecipeCardLoader() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(350.dp)
    ) {
        SkeletonLoader(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonLoader(
                modifier = Modifier
                    .height(50.dp)
                    .weight(1.5f)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            SkeletonLoader(
                modifier = Modifier
                    .height(30.dp)
                    .weight(0.5f)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonLoader(
                modifier = Modifier
                    .height(20.dp)
                    .weight(0.5f)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            SkeletonLoader(
                modifier = Modifier
                    .height(20.dp)
                    .weight(0.5f)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun RecipeCardLoaderPreview() {
    EZRecipesTheme {
        Surface {
            RecipeCardLoader()
        }
    }
}
