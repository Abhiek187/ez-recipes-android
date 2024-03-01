package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.RecipeFilter
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

// Connects the ViewModel to the DataSource
// Using dependency injection for happy little tests
class RecipeRepository(private val recipeService: RecipeService) {
    private fun <T> parseResponse(response: Response<T>): RecipeResult<T> {
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

    suspend fun getRecipesByFilter(filter: RecipeFilter): RecipeResult<List<Recipe>> {
        return try {
            val response = recipeService.getRecipesByFilter(filter)
            parseResponse(response)
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            RecipeResult.Error(recipeError)
        }
    }

    suspend fun getRandomRecipe(): RecipeResult<Recipe> {
        return try {
            val response = recipeService.getRandomRecipe()
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            return RecipeResult.Error(recipeError)
        }
    }

    suspend fun getRecipeById(id: Int): RecipeResult<Recipe> {
        return try {
            val response = recipeService.getRecipeById(id)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            return RecipeResult.Error(recipeError)
        }
    }
}
