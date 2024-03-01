package com.abhiek.ezrecipes.data.terms

import com.abhiek.ezrecipes.data.models.RecipeError

sealed class TermsResult<out T> {
    data class Success<T>(val response: T): TermsResult<T>()
    data class Error(val recipeError: RecipeError): TermsResult<Nothing>()
}
