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

    var recipeFilter by mutableStateOf(RecipeFilter())
    var recipes by mutableStateOf<List<Recipe>>(listOf())
    var isLoading by mutableStateOf(false)
    var isRecipeLoaded by mutableStateOf(false)
    var noRecipesFound by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)

    fun searchRecipes() {
        job = viewModelScope.launch {
            noRecipesFound = false
            isLoading = true
            val result = recipeRepository.getRecipesByFilter(recipeFilter)
            isLoading = false

            when (result) {
                is RecipeResult.Success -> {
                    recipes = result.response
                    recipeError = null
                    isRecipeLoaded = recipes.isNotEmpty()
                    noRecipesFound = recipes.isEmpty()
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
