package com.abhiek.ezrecipes.data.terms

import android.util.Log
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.serialization.json.Json
import retrofit2.Response

class TermsRepository(
    private val termsService: TermsService,
    private val dataStoreService: DataStoreService
) {
    companion object {
        private const val TAG = "TermsRepository"
    }

    private fun <T> parseResponse(response: Response<T>): TermsResult<T> {
        // isSuccessful means a 2xx response code
        val responseBody = response.body()
        val errorBody = response.errorBody()

        return if (response.isSuccessful && responseBody != null) {
            TermsResult.Success(responseBody)
        } else if (errorBody != null) {
            val errorString = errorBody.string()

            val recipeError = try {
                // Try to parse the response as a RecipeError
                Json.decodeFromString<RecipeError>(errorString)
            } catch (_: Exception) {
                // Otherwise, set the error property as the raw error string
                RecipeError(errorString)
            }

            TermsResult.Error(recipeError)
        } else {
            TermsResult.Error(RecipeError(Constants.UNKNOWN_ERROR))
        }
    }

    suspend fun fetchTerms(): TermsResult<List<Term>> {
        return try {
            val response = termsService.getTerms()
            parseResponse(response)
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            TermsResult.Error(recipeError)
        }
    }

    suspend fun getTerms(): List<Term> {
        // Check if terms need to be cached
        val cachedTerms = dataStoreService.getTerms()
        if (cachedTerms != null) {
            return cachedTerms
        }

        return when (val result = fetchTerms()) {
            is TermsResult.Success -> {
                dataStoreService.saveTerms(result.response)
                result.response
            }
            is TermsResult.Error -> {
                // No need to handle errors besides logging
                Log.w(TAG, "Failed to get terms :: error: ${result.recipeError}")
                listOf()
            }
        }
    }
}
