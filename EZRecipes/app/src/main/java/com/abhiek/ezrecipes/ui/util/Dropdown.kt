package com.abhiek.ezrecipes.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun <T> Dropdown(
    options: List<T>,
    value: T?,
    label: @Composable () -> Unit,
    customContent: (T) -> @Composable () -> Unit = { option -> { Text(option.toString()) } },
    onSelectOption: (option: T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Box {
        OutlinedTextField(
            value = value?.toString() ?: "", // don't show "null" for empty values
            onValueChange = {},
            // Don't allow the user to type in the text field, but allow it to be clickable
            enabled = false,
            label = label, // placeholder
            trailingIcon = {
                Icon(
                    if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = if (expanded) stringResource(R.string.collapse_alt)
                        else stringResource(R.string.expand_alt)
                )
            },
            // Make the dropdown appear clickable instead of grayed out
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .clickable { expanded = !expanded }
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                // Match the width of the menu with the text field
                .width(
                    with(LocalDensity.current) { textFieldSize.width.toDp() }
                )
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = customContent(option),
                    onClick = { onSelectOption(option) }
                )
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun DropdownPreview() {
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    EZRecipesTheme {
        Surface(modifier = Modifier.fillMaxHeight()) {
            Dropdown(
                options = (1..5).toList(),
                value = selectedOption,
                label = { Text(stringResource(R.string.rating_label)) },
                onSelectOption = { option -> selectedOption = option }
            )
        }
    }
}
