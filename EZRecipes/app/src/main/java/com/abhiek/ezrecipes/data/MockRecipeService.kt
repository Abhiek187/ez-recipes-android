package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Send hardcoded recipe responses for tests
// Using an object to create a singleton to pass to the repository
object MockRecipeService: RecipeService {
    var isSuccess = true // controls whether the mock API calls succeed or fail
    val recipe = Constants.Mocks.CHOCOLATE_CUPCAKE

    private const val recipeErrorString =
        "{\"error\":\"You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication\"}"
    val recipeError =
        RecipeError(error = "You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication")

    override suspend fun getRandomRecipe(): Response<Recipe> {
        return if (isSuccess) {
            Response.success(recipe)
        } else {
            Response.error(401, recipeErrorString.toResponseBody())
        }
    }

    override suspend fun getRecipeById(id: Int): Response<Recipe> {
        return if (isSuccess) {
            Response.success(recipe)
        } else {
            Response.error(401, recipeErrorString.toResponseBody())
        }
    }
}
