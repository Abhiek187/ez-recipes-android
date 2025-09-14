package com.abhiek.ezrecipes.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.RecipeFilter
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val recipeRepository: RecipeRepository
): ViewModel() {
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set
    var lastToken by mutableStateOf<String?>(null)
        private set

    var recipeFilter by mutableStateOf(RecipeFilter())
    var recipes by mutableStateOf<List<Recipe>>(listOf())
    var isLoading by mutableStateOf(false)
    var isRecipeLoaded by mutableStateOf(false)
    var noRecipesFound by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)

    fun searchRecipes(paginate: Boolean = false, fromFilterForm: Boolean = false) {
        job = viewModelScope.launch {
            noRecipesFound = false
            recipeFilter.token = if (paginate) lastToken else null

            isLoading = true
            val result = recipeRepository.getRecipesByFilter(recipeFilter)
            isLoading = false

            when (result) {
                is RecipeResult.Success -> {
                    // Append results if paginating, replace otherwise
                    if (paginate) {
                        recipes += result.response
                    } else {
                        recipes = result.response
                    }

                    recipeError = null
                    // isRecipeLoaded only applies when waiting for SearchResults from FilterForm
                    isRecipeLoaded = fromFilterForm && !paginate && recipes.isNotEmpty()
                    noRecipesFound = recipes.isEmpty()
                    // Prevent subsequent calls if there are no more results
                    lastToken = result.response.lastOrNull()?.token
                        ?: result.response.lastOrNull()?._id
                }
                is RecipeResult.Error -> {
                    recipes = listOf()
                    recipeError = result.recipeError
                    showRecipeAlert = job?.isCancelled == false
                }
            }
        }
    }
}
