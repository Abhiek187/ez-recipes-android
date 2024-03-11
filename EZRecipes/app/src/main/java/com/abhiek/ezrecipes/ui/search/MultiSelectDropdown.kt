package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.Cuisine
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
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
                    contentDescription = null // TODO: add text explaining if the menu is expanded or collapsed
                )
            },
            modifier = Modifier
                .padding(16.dp)
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = { onSelectOption(option) }
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

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun MultiSelectDropdownDemo() {
    val recipeService = MockRecipeService
    val viewModel = SearchViewModel(RecipeRepository(recipeService))

    EZRecipesTheme {
        Surface {
            MultiSelectDropdown(
                options = Cuisine.entries,
                value = viewModel.recipeFilter.culture,
                label = { Text(stringResource(R.string.culture_label)) },
                onSelectOption = {}
            )
        }
    }
}
