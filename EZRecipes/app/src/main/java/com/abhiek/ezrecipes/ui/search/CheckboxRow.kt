package com.abhiek.ezrecipes.ui.search

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
            style = MaterialTheme.typography.subtitle1
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null
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
