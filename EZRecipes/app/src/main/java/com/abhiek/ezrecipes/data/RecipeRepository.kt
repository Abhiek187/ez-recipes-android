package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import retrofit2.Response

// Connects the ViewModel to the DataSource
// Using dependency injection for happy little tests
class RecipeRepository(private val recipeService: RecipeService) {
    suspend fun getRandomRecipe(): Result<Recipe> {
        val response: Response<Recipe>

        try {
            response = recipeService.getRandomRecipe()
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            return Result.failure(error)
        }

        // isSuccessful means a 2xx response code
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception(response.errorBody()?.string()))
        }
    }

    suspend fun getRecipeById(id: Int): Result<Recipe> {
        val response: Response<Recipe>

        try {
            response = recipeService.getRecipeById(id)
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            return Result.failure(error)
        }

        // isSuccessful means a 2xx response code
        return if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception(response.errorBody()?.string()))
        }
    }
}
