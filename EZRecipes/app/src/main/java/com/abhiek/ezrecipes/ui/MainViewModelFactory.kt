package com.abhiek.ezrecipes.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService
import com.abhiek.ezrecipes.data.storage.AppDatabase

// Required to initialize a ViewModel with a non-empty constructor
class MainViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.instance,
                    recentRecipeDao = AppDatabase.getInstance(context).recentRecipeDao()
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
