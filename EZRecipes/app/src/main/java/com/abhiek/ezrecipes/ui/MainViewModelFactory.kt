package com.abhiek.ezrecipes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService

// Required to initialize a ViewModel with a non-empty constructor
class MainViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.instance
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
