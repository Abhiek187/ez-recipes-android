package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.SpiceLevel
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun <T> MultiSelectDropdown(
    options: List<T>,
    value: List<T>,
    label: @Composable () -> Unit,
    onSelectOption: (option: T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    Box {
        OutlinedTextField(
            // Show all the options that were selected
            value = value.joinToString(", "),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledBorderColor = MaterialTheme.colors.onSurface,
                disabledLabelColor = MaterialTheme.colors.onSurface,
                disabledTextColor = MaterialTheme.colors.onSurface,
                disabledTrailingIconColor = MaterialTheme.colors.onSurface
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
                    onClick = { onSelectOption(option) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(option.toString())
                        Checkbox(
                            checked = value.contains(option),
                            onCheckedChange = null
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun MultiSelectDropdownPreview() {
    var selectedOptions by remember { mutableStateOf(listOf<SpiceLevel>()) }

    EZRecipesTheme {
        Surface(modifier = Modifier.fillMaxHeight()) {
            MultiSelectDropdown(
                options = SpiceLevel.entries.toList(),
                value = selectedOptions,
                label = { Text(stringResource(R.string.spice_label)) },
                onSelectOption = { option ->
                    selectedOptions = if (selectedOptions.contains(option)) {
                        selectedOptions - option
                    } else {
                        selectedOptions + option
                    }
                }
            )
        }
    }
}
