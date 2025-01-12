package com.abhiek.ezrecipes.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.ChefService
import com.abhiek.ezrecipes.data.interceptors.CacheInterceptor
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeService
import com.abhiek.ezrecipes.data.storage.DataStoreService
import java.util.concurrent.TimeUnit

class ProfileViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                chefRepository = ChefRepository(
                    chefService = ChefService.getInstance(
                        cacheInterceptor = CacheInterceptor(
                            context = context,
                            sizeInMB = 1,
                            age = 5,
                            units = TimeUnit.MINUTES
                        )
                    )
                ),
                recipeRepository = RecipeRepository(
                    recipeService = RecipeService.instance
                ),
                dataStoreService = DataStoreService(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
