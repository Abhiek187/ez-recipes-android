package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.Cuisine
import com.abhiek.ezrecipes.data.models.MealType
import com.abhiek.ezrecipes.data.models.SpiceLevel
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.getActivity

@Composable
fun FilterForm(searchViewModel: SearchViewModel) {
    var caloriesExceedMax by remember { mutableStateOf(false) }
    var caloriesInvalidRange by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP &&
                context.getActivity()?.isChangingConfigurations != true) {
                searchViewModel.job?.cancel()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = searchViewModel.recipeFilter.query,
            onValueChange = { searchViewModel.recipeFilter.query = it },
            label = { Text(stringResource(R.string.query_section)) },
            placeholder = { Text(stringResource(R.string.query_placeholder)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Text(text = stringResource(R.string.filter_section))

        Row {
            TextField(
                value = searchViewModel.recipeFilter.minCals?.toString() ?: "",
                onValueChange = {
                    val newValue = it.toInt()
                    searchViewModel.recipeFilter.minCals = newValue
                    caloriesExceedMax = newValue > Constants.MAX_CALS ||
                            (searchViewModel.recipeFilter.maxCals
                                ?: Constants.MIN_CALS) > Constants.MAX_CALS
                    caloriesInvalidRange =
                        newValue > (searchViewModel.recipeFilter.maxCals ?: Int.MAX_VALUE)
                },
                placeholder = { Text(stringResource(R.string.min_cals_placeholder)) },
                isError = caloriesExceedMax || caloriesInvalidRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(stringResource(R.string.calorie_label))
            TextField(
                value = searchViewModel.recipeFilter.maxCals?.toString() ?: "",
                onValueChange = {
                    val newValue = it.toInt()
                    searchViewModel.recipeFilter.maxCals = newValue
                    caloriesExceedMax = newValue != Int.MAX_VALUE && newValue > Constants.MAX_CALS ||
                            (searchViewModel.recipeFilter.minCals
                                ?: Constants.MIN_CALS) > Constants.MAX_CALS
                    caloriesInvalidRange =
                        newValue < (searchViewModel.recipeFilter.minCals ?: Int.MIN_VALUE)
                },
                placeholder = { Text(stringResource(R.string.max_cals_placeholder)) },
                isError = caloriesExceedMax || caloriesInvalidRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(stringResource(R.string.calorie_unit))
        }

        Row {
            Text(
                text = stringResource(R.string.vegetarian_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.vegetarian,
                onCheckedChange = { searchViewModel.recipeFilter.vegetarian = it }
            )
        }
        Row {
            Text(
                text = stringResource(R.string.vegan_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.vegan,
                onCheckedChange = { searchViewModel.recipeFilter.vegan = it }
            )
        }
        Row {
            Text(
                text = stringResource(R.string.gluten_free_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.glutenFree,
                onCheckedChange = { searchViewModel.recipeFilter.glutenFree = it }
            )
        }
        Row {
            Text(
                text = stringResource(R.string.healthy_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.healthy,
                onCheckedChange = { searchViewModel.recipeFilter.healthy = it }
            )
        }
        Row {
            Text(
                text = stringResource(R.string.cheap_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.cheap,
                onCheckedChange = { searchViewModel.recipeFilter.cheap = it }
            )
        }
        Row {
            Text(
                text = stringResource(R.string.sustainable_label)
            )
            Checkbox(
                checked = searchViewModel.recipeFilter.sustainable,
                onCheckedChange = { searchViewModel.recipeFilter.sustainable = it }
            )
        }

        MultiSelectDropdown(
            options = SpiceLevel.entries.filter { spiceLevel ->
                // Don't filter by unknown
                spiceLevel != SpiceLevel.UNKNOWN
            },
            value = searchViewModel.recipeFilter.spiceLevel,
            label = { Text(stringResource(R.string.spice_label)) },
            onSelectOption = { spiceLevel ->
                searchViewModel.recipeFilter.spiceLevel = if (searchViewModel.recipeFilter.spiceLevel.contains(spiceLevel)) {
                    searchViewModel.recipeFilter.spiceLevel - spiceLevel
                } else {
                    searchViewModel.recipeFilter.spiceLevel + spiceLevel
                }
            }
        )
        MultiSelectDropdown(
            options = MealType.entries.filter { mealType ->
                mealType != MealType.UNKNOWN
            }.sortedBy { mealType ->
                // Allow the meal types to be sorted for ease of reference
                mealType.name
            },
            value = searchViewModel.recipeFilter.type,
            label = { Text(stringResource(R.string.type_label)) },
            onSelectOption = { mealType ->
                searchViewModel.recipeFilter.type = if (searchViewModel.recipeFilter.type.contains(mealType)) {
                    searchViewModel.recipeFilter.type - mealType
                } else {
                    searchViewModel.recipeFilter.type + mealType
                }
            }
        )
        MultiSelectDropdown(
            options = Cuisine.entries.filter { cuisine ->
                cuisine != Cuisine.UNKNOWN
            }.sortedBy { cuisine ->
                cuisine.name
            },
            value = searchViewModel.recipeFilter.culture,
            label = { Text(stringResource(R.string.culture_label)) },
            onSelectOption = { cuisine ->
                searchViewModel.recipeFilter.culture = if (searchViewModel.recipeFilter.culture.contains(cuisine)) {
                    searchViewModel.recipeFilter.culture - cuisine
                } else {
                    searchViewModel.recipeFilter.culture + cuisine
                }
            }
        )

        SubmitButton(
            searchViewModel = searchViewModel,
            enabled = !caloriesExceedMax && !caloriesInvalidRange && !searchViewModel.isLoading
        )
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun FilterFormPreview() {
    val recipeService = MockRecipeService
    val viewModel = SearchViewModel(RecipeRepository(recipeService))

    EZRecipesTheme {
        Surface {
            FilterForm(viewModel)
        }
    }
}
