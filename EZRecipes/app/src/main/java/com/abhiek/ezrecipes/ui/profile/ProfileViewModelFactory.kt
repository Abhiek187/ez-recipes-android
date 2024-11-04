package com.abhiek.ezrecipes.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.ChefService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService

class ProfileViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                chefRepository = ChefRepository(
                    chefService = ChefService.instance
                ),
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.instance
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
