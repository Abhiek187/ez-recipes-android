package com.abhiek.ezrecipes.ui.search

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.abhiek.ezrecipes.ui.theme.Amber700
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.CheckboxRow
import com.abhiek.ezrecipes.ui.util.Dropdown
import com.abhiek.ezrecipes.ui.util.FormError
import com.abhiek.ezrecipes.ui.util.MultiSelectDropdown
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.getActivity
import kotlin.math.floor

@Composable
fun FilterForm(
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier,
    onNavigateToResults: () -> Unit
) {
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

    if (searchViewModel.isRecipeLoaded) {
        LaunchedEffect(searchViewModel.recipes) {
            if (searchViewModel.recipes.isNotEmpty()) {
                onNavigateToResults()
                searchViewModel.isRecipeLoaded = false
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(16.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = searchViewModel.recipeFilter.query,
            onValueChange = {
                // Like setState(), the whole object must be passed to recompose
                searchViewModel.recipeFilter = searchViewModel.recipeFilter.copy(query = it)
            },
            label = { Text(stringResource(R.string.query_section)) },
            placeholder = { Text(stringResource(R.string.query_placeholder)) },
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchViewModel.recipeFilter.minCals?.toString() ?: "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            // Treat empty inputs as null
                            searchViewModel.recipeFilter =
                                searchViewModel.recipeFilter.copy(minCals = null)
                            caloriesExceedMax = (searchViewModel.recipeFilter.maxCals
                                ?: Constants.MIN_CALS) > Constants.MAX_CALS
                            caloriesInvalidRange = false
                            return@TextField
                        }

                        // Disregard decimals (to be more consistent with other platforms)
                        val parsedValue = it.toFloatOrNull() ?: return@TextField
                        val newValue = floor(parsedValue).toInt()

                        searchViewModel.recipeFilter =
                            searchViewModel.recipeFilter.copy(minCals = newValue)
                        caloriesExceedMax = newValue > Constants.MAX_CALS ||
                                (searchViewModel.recipeFilter.maxCals
                                    ?: Constants.MIN_CALS) > Constants.MAX_CALS
                        caloriesInvalidRange =
                            newValue > (searchViewModel.recipeFilter.maxCals ?: Int.MAX_VALUE)
                    },
                    placeholder = { Text(stringResource(R.string.min_cals_placeholder)) },
                    isError = caloriesExceedMax || caloriesInvalidRange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.width(75.dp)
                )
                Text(stringResource(R.string.calorie_label))
                TextField(
                    value = searchViewModel.recipeFilter.maxCals?.toString() ?: "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            searchViewModel.recipeFilter =
                                searchViewModel.recipeFilter.copy(maxCals = null)
                            caloriesExceedMax = (searchViewModel.recipeFilter.minCals
                                ?: Constants.MIN_CALS) > Constants.MAX_CALS
                            caloriesInvalidRange = false
                            return@TextField
                        }

                        val parsedValue = it.toFloatOrNull() ?: return@TextField
                        val newValue = floor(parsedValue).toInt()

                        searchViewModel.recipeFilter =
                            searchViewModel.recipeFilter.copy(maxCals = newValue)
                        caloriesExceedMax =
                            newValue != Int.MAX_VALUE && newValue > Constants.MAX_CALS ||
                                    (searchViewModel.recipeFilter.minCals
                                        ?: Constants.MIN_CALS) > Constants.MAX_CALS
                        caloriesInvalidRange =
                            newValue < (searchViewModel.recipeFilter.minCals ?: Int.MIN_VALUE)
                    },
                    placeholder = { Text(stringResource(R.string.max_cals_placeholder)) },
                    isError = caloriesExceedMax || caloriesInvalidRange,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.width(75.dp)
                )
                Text(stringResource(R.string.calorie_unit))
            }
            // Form errors
            FormError(
                on = caloriesExceedMax,
                message = stringResource(R.string.calorie_exceed_max_error)
            )
            FormError(
                on = caloriesInvalidRange,
                message = stringResource(R.string.calorie_invalid_range_error)
            )
        }

        Column {
            CheckboxRow(
                stringId = R.string.vegetarian_label,
                checked = searchViewModel.recipeFilter.vegetarian,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(vegetarian = it)
                }
            )
            CheckboxRow(
                stringId = R.string.vegan_label,
                checked = searchViewModel.recipeFilter.vegan,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(vegan = it)
                }
            )
            CheckboxRow(
                stringId = R.string.gluten_free_label,
                checked = searchViewModel.recipeFilter.glutenFree,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(glutenFree = it)
                }
            )
            CheckboxRow(
                stringId = R.string.healthy_label,
                checked = searchViewModel.recipeFilter.healthy,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(healthy = it)
                }
            )
            CheckboxRow(
                stringId = R.string.cheap_label,
                checked = searchViewModel.recipeFilter.cheap,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(cheap = it)
                }
            )
            CheckboxRow(
                stringId = R.string.sustainable_label,
                checked = searchViewModel.recipeFilter.sustainable,
                onCheckedChange = {
                    searchViewModel.recipeFilter =
                        searchViewModel.recipeFilter.copy(sustainable = it)
                }
            )
        }

        Dropdown(
            options = (1..5).toList(),
            value = searchViewModel.recipeFilter.rating,
            label = { Text(stringResource(R.string.rating_label)) },
            customContent = { option -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..option) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (isSystemInDarkTheme()) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                Amber700
                            }
                        )
                    }
                    Text(
                        text = option.toString(),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            } },
            onSelectOption = { option ->
                searchViewModel.recipeFilter = searchViewModel.recipeFilter.copy(rating = option)
            }
        )
        MultiSelectDropdown(
            options = SpiceLevel.entries.filter { spiceLevel ->
                // Don't filter by unknown
                spiceLevel != SpiceLevel.UNKNOWN
            },
            value = searchViewModel.recipeFilter.spiceLevel,
            label = { Text(stringResource(R.string.spice_label)) },
            onSelectOption = { spiceLevel ->
                searchViewModel.recipeFilter = searchViewModel.recipeFilter.copy(
                    spiceLevel = toggleList(searchViewModel.recipeFilter.spiceLevel, spiceLevel)
                )
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
                searchViewModel.recipeFilter = searchViewModel.recipeFilter.copy(
                    type = toggleList(searchViewModel.recipeFilter.type, mealType)
                )
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
                searchViewModel.recipeFilter = searchViewModel.recipeFilter.copy(
                    culture = toggleList(searchViewModel.recipeFilter.culture, cuisine)
                )
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubmitButton(
                searchViewModel = searchViewModel,
                enabled = !caloriesExceedMax && !caloriesInvalidRange && !searchViewModel.isLoading
            )
            if (searchViewModel.noRecipesFound) {
                Text(
                    text = stringResource(R.string.no_results),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Adds the item if present in the list, otherwise removes the item
 */
private fun <T> toggleList(list: List<T>, item: T): List<T> =
    if (list.contains(item)) {
        list - item
    } else {
        list + item
    }

private data class FilterFormState(
    val maxError: Boolean = false,
    val rangeError: Boolean = false,
    val isLoading: Boolean = false,
    val noResults: Boolean = false
)

private class FilterFormPreviewParameterProvider: PreviewParameterProvider<FilterFormState> {
    override val values = sequenceOf(
        FilterFormState(),
        FilterFormState(maxError = true),
        FilterFormState(rangeError = true),
        FilterFormState(isLoading = true),
        FilterFormState(noResults = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun FilterFormPreview(
    @PreviewParameter(FilterFormPreviewParameterProvider::class) state: FilterFormState
) {
    val recipeService = MockRecipeService
    val viewModel = SearchViewModel(RecipeRepository(recipeService))

    val (maxError, rangeError, isLoading, noResults) = state
    if (maxError) {
        viewModel.recipeFilter.maxCals = 2001
    }
    if (rangeError) {
        viewModel.recipeFilter.minCals = 200
        viewModel.recipeFilter.maxCals = 100
    }
    viewModel.isLoading = isLoading
    viewModel.noRecipesFound = noResults

    EZRecipesTheme {
        Surface {
            FilterForm(viewModel) {}
        }
    }
}
