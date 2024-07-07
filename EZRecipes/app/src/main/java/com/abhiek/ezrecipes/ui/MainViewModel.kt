package com.abhiek.ezrecipes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Connects the View to the Repository
class MainViewModel(
    private val recipeRepository: RecipeRepository
): ViewModel() {
    // Only expose a read-only copy of the state to the View
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set

    var recipe by mutableStateOf<Recipe?>(null)
    var isLoading by mutableStateOf(false)
    // Alerts the home screen to navigate to the recipe screen
    var isRecipeLoaded by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)
    var recentRecipes by mutableStateOf<List<RecentRecipe>>(listOf())

    private fun updateRecipeProps(
        result: RecipeResult<Recipe>,
        fromHome: Boolean
    ) {
        // Set all the ViewModel properties based on the API result
        when (result) {
            is RecipeResult.Success -> {
                recipe = result.response
                recipeError = null
                isRecipeLoaded = fromHome
                showRecipeAlert = false
            }
            is RecipeResult.Error -> {
                recipe = null
                recipeError = result.recipeError
                isRecipeLoaded = false
                // Don't show an alert if the request was intentionally cancelled
                showRecipeAlert = job?.isCancelled == false
            }
        }
    }

    fun getRandomRecipe(fromHome: Boolean = false) {
        job = viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRandomRecipe()
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }

    fun getRecipeById(id: Int, fromHome: Boolean = false) {
        job = viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRecipeById(id)
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }

    fun fetchRecentRecipes() {
        viewModelScope.launch {
            recentRecipes = recipeRepository.fetchRecentRecipes()
        }
    }

    fun saveRecentRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.saveRecentRecipe(recipe)
        }
    }
}
