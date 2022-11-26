package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

// Connects the ViewModel to the DataSource
// Using dependency injection for happy little tests
class RecipeRepository(private val recipeService: RecipeService) {
    suspend fun getRandomRecipe(): RecipeResult {
        val response: Response<Recipe>

        try {
            response = recipeService.getRandomRecipe()
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            return RecipeResult.Error(recipeError)
        }

        // isSuccessful means a 2xx response code
        return if (response.isSuccessful && response.body() != null) {
            RecipeResult.Success(response.body()!!)
        } else {
            val errorString = response.errorBody()?.string()

            val recipeError = try {
                // Try to parse the response as a RecipeError
                Gson().fromJson(errorString, RecipeError::class.java)
            } catch (error: JsonSyntaxException) {
                // Otherwise, set the error property as the raw error string
                RecipeError(errorString ?: Constants.UNKNOWN_ERROR)
            }

            return RecipeResult.Error(recipeError)
        }
    }

    suspend fun getRecipeById(id: Int): RecipeResult {
        val response: Response<Recipe>

        try {
            response = recipeService.getRecipeById(id)
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            return RecipeResult.Error(recipeError)
        }

        // isSuccessful means a 2xx response code
        return if (response.isSuccessful && response.body() != null) {
            RecipeResult.Success(response.body()!!)
        } else {
            val errorString = response.errorBody()?.string()

            val recipeError = try {
                // Try to parse the response as a RecipeError
                Gson().fromJson(errorString, RecipeError::class.java)
            } catch (error: JsonSyntaxException) {
                // Otherwise, set the error property as the raw error string
                RecipeError(errorString ?: Constants.UNKNOWN_ERROR)
            }

            return RecipeResult.Error(recipeError)
        }
    }
}
