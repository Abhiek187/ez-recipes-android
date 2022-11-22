package com.abhiek.ezrecipes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.RecipeRepository
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
        private set
    // Let the view change this property
    var isRecipeLoaded by mutableStateOf(false)

    fun getRandomRecipe() {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRandomRecipe()
            isLoading = false

            response.fold(
                onSuccess = { recipe ->
                    this@MainViewModel.recipe = recipe
                    recipeError = null
                    isRecipeLoaded = true
                },
                onFailure = { error ->
                    recipe = null
                    recipeError = RecipeError(
                        error.localizedMessage ?:
                        "Something went terribly wrong. Please submit a bug report to https://github.com/Abhiek187/ez-recipes-android/issues"
                    )
                }
            )
        }
    }

    fun getRecipeById(id: Int) {
        viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRecipeById(id)
            isLoading = false

            response.fold(
                onSuccess = { recipe ->
                    this@MainViewModel.recipe = recipe
                    recipeError = null
                },
                onFailure = { error ->
                    recipe = null
                    recipeError = RecipeError(
                        error.localizedMessage ?:
                        "Something went terribly wrong. Please submit a bug report to https://github.com/Abhiek187/ez-recipes-android/issues"
                    )
                }
            )
        }
    }
}
