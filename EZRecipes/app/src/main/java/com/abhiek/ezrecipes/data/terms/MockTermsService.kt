package com.abhiek.ezrecipes.data.terms

import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object MockTermsService: TermsService {
    var isSuccess = true
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
