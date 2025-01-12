package com.abhiek.ezrecipes.ui.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService

class SearchViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.getInstance(context)
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
