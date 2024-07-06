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
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Connects the View to the Repository
class MainViewModel(
    private val recipeRepository: RecipeRepository,
    private val recentRecipeDao: RecentRecipeDao
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

                saveRecentRecipe(result.response)
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

    private fun saveRecentRecipe(recipe: Recipe) {
        viewModelScope.launch {
            // If the recipe already exists, replace the timestamp with the current time
            val existingRecipe = recentRecipeDao.getRecipeById(recipe.id)

            if (existingRecipe != null) {
                existingRecipe.timestamp = System.currentTimeMillis()
                recentRecipeDao.insert(existingRecipe)
                return@launch
            }

            // If there are too many recipes, delete the oldest recipe
            val recipes = recentRecipeDao.getAll()

            if (recipes.size >= Constants.MAX_RECENT_RECIPES) {
                val oldestRecipe = recipes.last()
                recentRecipeDao.delete(oldestRecipe)
            }

            val newRecipe = RecentRecipe(
                id = recipe.id,
                timestamp = System.currentTimeMillis(),
                recipe = recipe
            )
            recentRecipeDao.insert(newRecipe)
        }
    }
}
