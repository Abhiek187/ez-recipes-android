package com.abhiek.ezrecipes.data.terms

import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Send hardcoded recipe responses for tests
// Using an object to create a singleton to pass to the repository
object MockTermsService: TermsService {
    var isSuccess = true // controls whether the mock API calls succeed or fail
    val terms = Constants.Mocks.TERMS

    private const val RECIPE_ERROR_STRING =
        "{\"error\":\"You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication\"}"
    val recipeError =
        RecipeError(error = "You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication")

    override suspend fun getTerms(): Response<List<Term>> {
        return if (isSuccess) {
            Response.success(terms)
        } else {
            Response.error(401, RECIPE_ERROR_STRING.toResponseBody())
        }
    }
}
