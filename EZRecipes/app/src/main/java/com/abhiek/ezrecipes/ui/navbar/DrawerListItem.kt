package com.abhiek.ezrecipes.ui.navbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun DrawerListItem(item: Tab, selected: Boolean, onItemClick: () -> Unit) {
    // Highlight the selected drawer item
    val backgroundColor = if (selected) MaterialTheme.colorScheme.tertiary else Color.Transparent

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
            contentDescription = null,
            modifier = Modifier
                .height(32.dp)
                .width(32.dp),
            // In dark mode, black contrasts with yellow better than white
            tint = if (selected) Color.Black else MaterialTheme.colorScheme.onBackground
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        Text(
            text = stringResource(item.resourceId),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp
            ),
            color = if (selected) Color.Black else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable(onClick = onItemClick)
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
            DrawerListItem(item = Tab.Home, selected = true) {}
            DrawerListItem(item = Tab.Search, selected = false) {}
            DrawerListItem(item = Tab.Glossary, selected = false) {}
        }
    }
}
