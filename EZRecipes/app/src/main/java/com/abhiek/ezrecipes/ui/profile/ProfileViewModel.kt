package com.abhiek.ezrecipes.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.ChefResult
import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val chefRepository: ChefRepository,
    private val recipeRepository: RecipeRepository
): ViewModel() {
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set

    var authState by mutableStateOf(AuthState.UNAUTHENTICATED)
    var isLoading by mutableStateOf(false)
    var chef by mutableStateOf<Chef?>(null)
    var favoriteRecipes by mutableStateOf<List<Recipe>>(listOf())
    var recentRecipes by mutableStateOf<List<Recipe>>(listOf())
    var ratedRecipes by mutableStateOf<List<Recipe>>(listOf())
    var openLoginDialog by mutableStateOf(false)
    var showAlert by mutableStateOf(false)

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    fun createAccount(username: String, password: String) {
        val loginCredentials = LoginCredentials(username, password)

        job = viewModelScope.launch {
            isLoading = true
            val result = chefRepository.createChef(loginCredentials)
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val loginResponse = result.response
                    println("loginResponse: $loginResponse")
                    recipeError = null
                    showAlert = false
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun verifyEmail(token: String) {
        job = viewModelScope.launch {
            isLoading = true
            val result = chefRepository.verifyEmail(token)
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val emailResponse = result.response
                    println("emailResponse: $emailResponse")
                    recipeError = null
                    showAlert = false
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

//    fun getRecipeById(id: Int) {
//        job = viewModelScope.launch {
//            isLoading = true
//            val result = recipeRepository.getRecipeById(id)
//            isLoading = false
//
//            when (result) {
//                is RecipeResult.Success -> {
//                    recipe = result.response
//                    recipeError = null
//                }
//                is RecipeResult.Error -> {
//                    recipe = null
//                    recipeError = result.recipeError
//                }
//            }
//        }
//    }

    fun getAllChefRecipes() {
        if (chef == null) return

        for (id in chef!!.favoriteRecipes) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        favoriteRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }

        for ((id, timestamp) in chef!!.recentRecipes.entries) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        recentRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }

        for ((id, rating) in chef!!.ratings.entries) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        ratedRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }
    }
}
