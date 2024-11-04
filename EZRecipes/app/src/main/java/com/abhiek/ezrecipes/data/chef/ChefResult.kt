package com.abhiek.ezrecipes.data.chef

import com.abhiek.ezrecipes.data.models.RecipeError

sealed class ChefResult<out T> {
    data class Success<T>(val response: T): ChefResult<T>()
    data class Error(val recipeError: RecipeError): ChefResult<Nothing>()
}
