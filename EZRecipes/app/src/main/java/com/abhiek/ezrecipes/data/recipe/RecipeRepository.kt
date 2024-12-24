package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

// Connects the ViewModel to the DataSource
// Using dependency injection for happy little tests
class RecipeRepository(
    private val recipeService: RecipeService,
    private val recentRecipeDao: RecentRecipeDao? = null
) {
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
            val response = recipeService.getRecipesByFilter(
                vegetarian = if (filter.vegetarian) "vegetarian" else null,
                vegan = if (filter.vegan) "vegan" else null,
                glutenFree = if (filter.glutenFree) "gluten-free" else null,
                healthy = if (filter.healthy) "healthy" else null,
                cheap = if (filter.cheap) "cheap" else null,
                sustainable = if (filter.sustainable) "sustainable" else null,
                spiceLevels = filter.spiceLevel,
                mealTypes = filter.type,
                cuisines = filter.culture,
                filters = filter.toMap(),
            )
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

    suspend fun updateRecipe(
        id: Int, fields: RecipeUpdate, token: String? = null
    ): RecipeResult<Token> {
        return try {
            val response = recipeService.updateRecipe(id, fields, token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            return RecipeResult.Error(recipeError)
        }
    }

    suspend fun fetchRecentRecipes(): List<RecentRecipe> {
        if (recentRecipeDao == null) return listOf()
        return recentRecipeDao.getAll()
    }

    suspend fun saveRecentRecipe(recipe: Recipe) {
        if (recentRecipeDao == null) return
        // If the recipe already exists, replace the timestamp with the current time
        // Also make sure all recipe stats are up-to-date
        val existingRecipe = recentRecipeDao.getRecipeById(recipe.id)

        if (existingRecipe != null) {
            existingRecipe.timestamp = System.currentTimeMillis()
            existingRecipe.recipe = recipe
            recentRecipeDao.insert(existingRecipe)
            return
        }

        // If there are too many recipes, delete the oldest recipe
        val recipes = recentRecipeDao.getAll()

        if (recipes.size >= Constants.MAX_RECENT_RECIPES) {
            val oldestRecipe = recipes.last()
            recentRecipeDao.delete(oldestRecipe)
        }

        val newRecipe = RecentRecipe(
            id = recipe.id,
            timestamp = System.currentTimeMillis(),
            recipe = recipe
        )
        recentRecipeDao.insert(newRecipe)
    }
}
