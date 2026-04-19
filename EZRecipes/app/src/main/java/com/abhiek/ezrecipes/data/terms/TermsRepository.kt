package com.abhiek.ezrecipes.data.terms

import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.serialization.json.Json
import retrofit2.Response

class TermsRepository(private val termsService: TermsService) {
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

    suspend fun getTerms(): TermsResult<List<Term>> {
        return try {
            val response = termsService.getTerms()
            parseResponse(response)
        } catch (error: Exception) {
            // Catch ConnectExceptions, UnknownHostExceptions, etc.
            val recipeError = RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
            TermsResult.Error(recipeError)
        }
    }
}
