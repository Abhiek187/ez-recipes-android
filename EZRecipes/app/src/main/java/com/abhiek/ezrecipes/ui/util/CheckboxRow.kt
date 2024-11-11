package com.abhiek.ezrecipes.ui.util

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun CheckboxRow(
    @StringRes stringId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Make both the text and checkbox clickable for a11y
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Text(
            text = stringResource(stringId),
            style = MaterialTheme.typography.titleMedium
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors().copy(
                checkedBoxColor = MaterialTheme.colorScheme.tertiary,
                checkedCheckmarkColor = MaterialTheme.colorScheme.onTertiary,
                checkedBorderColor = MaterialTheme.colorScheme.tertiary
            )
        )
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun CheckboxRowPreview() {
    EZRecipesTheme {
        Surface {
            CheckboxRow(
                stringId = R.string.vegetarian_label,
                checked = true,
                onCheckedChange = {}
            )
        }
    }
}
