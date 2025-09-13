package com.abhiek.ezrecipes.data.terms

import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Response

class TermsRepository(private val termsService: TermsService) {
    private fun <T> parseResponse(response: Response<T>): TermsResult<T> {
        // isSuccessful means a 2xx response code
        return if (response.isSuccessful && response.body() != null) {
            TermsResult.Success(response.body()!!)
        } else {
            val errorString = response.errorBody()?.string()

            val recipeError = try {
                // Try to parse the response as a RecipeError
                Gson().fromJson(errorString, RecipeError::class.java)
            } catch (_: JsonSyntaxException) {
                // Otherwise, set the error property as the raw error string
                RecipeError(errorString ?: Constants.UNKNOWN_ERROR)
            }

            return TermsResult.Error(recipeError)
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
