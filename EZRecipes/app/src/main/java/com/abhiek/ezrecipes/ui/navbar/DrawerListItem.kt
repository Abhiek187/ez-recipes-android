package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun DrawerListItem(item: DrawerItem, selected: Boolean, onItemClick: () -> Unit) {
    // Highlight the selected drawer item
    val backgroundColor = if (selected) MaterialTheme.colors.secondary else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .height(50.dp)
            .background(backgroundColor)
            .padding(start = 16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier
                .height(32.dp)
                .width(32.dp),
            // In dark mode, black contrasts with yellow better than white
            tint = if (selected) Color.Black else MaterialTheme.colors.onBackground
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Text(
            text = item.title,
            fontSize = 18.sp,
            color = if (selected) Color.Black else MaterialTheme.colors.onBackground
        )
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun DrawerListItemPreview() {
    EZRecipesTheme {
        Column {
            DrawerListItem(item = DrawerItem.Home, selected = true) {}
            DrawerListItem(item = DrawerItem.Recipe, selected = false) {}
        }
    }
}