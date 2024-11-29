package com.abhiek.ezrecipes.data.chef

import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

class ChefRepository(private val chefService: ChefService) {
    private fun <T> parseResponse(response: Response<T>): ChefResult<T> {
        // Empty bodies are ok as long as a 2xx response is returned
        return if (response.isSuccessful) {
            // Unit:Kotlin::Void:Java
            @Suppress("UNCHECKED_CAST")
            ChefResult.Success(response.body() ?: Unit as T)
        } else {
            val errorString = response.errorBody()?.string()

            val recipeError = try {
                // Try to parse the response as a RecipeError
                Gson().fromJson(errorString, RecipeError::class.java)
            } catch (error: JsonSyntaxException) {
                // Otherwise, set the error property as the raw error string
                RecipeError(errorString ?: Constants.UNKNOWN_ERROR)
            }

            return ChefResult.Error(recipeError)
        }
    }

    suspend fun getChef(token: String): ChefResult<Chef> {
        return try {
            val response = chefService.getChef(token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun createChef(credentials: LoginCredentials): ChefResult<LoginResponse> {
        return try {
            val response = chefService.createChef(credentials)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun updateChef(fields: ChefUpdate, token: String? = null): ChefResult<ChefEmailResponse> {
        return try {
            val response = chefService.updateChef(fields, token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun deleteChef(token: String): ChefResult<Void> {
        return try {
            val response = chefService.deleteChef(token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun verifyEmail(token: String): ChefResult<ChefEmailResponse> {
        return try {
            val response = chefService.verifyEmail(token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun login(credentials: LoginCredentials): ChefResult<LoginResponse> {
        return try {
            val response = chefService.login(credentials)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }

    suspend fun logout(token: String): ChefResult<Void> {
        return try {
            val response = chefService.logout(token)
            parseResponse(response)
        } catch (error: Exception) {
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            ChefResult.Error(recipeError)
        }
    }
}