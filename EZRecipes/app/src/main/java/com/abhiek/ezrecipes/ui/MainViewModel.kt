package com.abhiek.ezrecipes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.RecipeRepository
import com.abhiek.ezrecipes.data.models.Recipe
import kotlinx.coroutines.launch

// Connects the View to the Repository
class MainViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    // Only expose a read-only copy of the LiveData to the View
    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe

    fun getRandomRecipe() {
        viewModelScope.launch {
            val response = recipeRepository.getRandomRecipe()

            response.fold(
                // Can only update the LiveData's value on the main thread
                onSuccess = { recipe -> _recipe.postValue(recipe) },
                onFailure = { error -> println(error.localizedMessage) }
            )
        }
    }

    fun getRecipeById(id: String) {
        viewModelScope.launch {
            val response = recipeRepository.getRecipeById(id)

            response.fold(
                // Can only update the LiveData's value on the main thread
                onSuccess = { recipe -> _recipe.postValue(recipe) },
                onFailure = { error -> println(error.localizedMessage) }
            )
        }
    }
}
