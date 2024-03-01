package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.models.RecipeError

// Custom Result class to hold Recipe objects on success and RecipeError objects on error
sealed class RecipeResult<out T> {
    data class Success<T>(val response: T): RecipeResult<T>()
    data class Error(val recipeError: RecipeError): RecipeResult<Nothing>()
}
