package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError

// Custom Result class to hold Recipe objects on success and RecipeError objects on error
sealed class RecipeResult {
    data class Success(val recipe: Recipe): RecipeResult()
    data class Error(val recipeError: RecipeError): RecipeResult()
}
