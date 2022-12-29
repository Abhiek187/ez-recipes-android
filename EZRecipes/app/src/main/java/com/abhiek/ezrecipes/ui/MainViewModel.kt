package com.abhiek.ezrecipes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.RecipeRepository
import com.abhiek.ezrecipes.data.RecipeResult
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import kotlinx.coroutines.launch

// Connects the View to the Repository
class MainViewModel(
    private val recipeRepository: RecipeRepository
): ViewModel() {
    // Only expose a read-only copy of the state to the View
    var recipe by mutableStateOf<Recipe?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set

    var isLoading by mutableStateOf(false)
    // Alerts the home screen to navigate to the recipe screen
    var isRecipeLoaded by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)

    private fun updateRecipeProps(
        response: RecipeResult,
        fromHome: Boolean
    ) {
        // Set all the ViewModel properties based on the API result
        when (response) {
            is RecipeResult.Success -> {
                recipe = response.recipe
                recipeError = null
                isRecipeLoaded = fromHome
                showRecipeAlert = false
            }
            is RecipeResult.Error -> {
                recipe = null
                recipeError = response.recipeError
                isRecipeLoaded = false
                showRecipeAlert = true
            }
        }
    }

    fun getRandomRecipe(fromHome: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRandomRecipe()
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }

    fun getRecipeById(id: Int, fromHome: Boolean = false) {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRecipeById(id)
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }
}
