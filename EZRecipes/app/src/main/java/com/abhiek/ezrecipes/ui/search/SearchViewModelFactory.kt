package com.abhiek.ezrecipes.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService

class SearchViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.instance
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
