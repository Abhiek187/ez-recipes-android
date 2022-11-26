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
    var isRecipeLoaded by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)

    fun getRandomRecipe() {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRandomRecipe()
            isLoading = false

            when (response) {
                is RecipeResult.Success -> {
                    recipe = response.recipe
                    recipeError = null
                    isRecipeLoaded = true
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
    }

    fun getRecipeById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRecipeById(id)
            isLoading = false

            when (response) {
                is RecipeResult.Success -> {
                    recipe = response.recipe
                    recipeError = null
                    isRecipeLoaded = true
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
    }
}
