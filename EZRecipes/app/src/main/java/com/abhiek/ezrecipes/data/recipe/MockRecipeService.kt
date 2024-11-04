package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Send hardcoded recipe responses for tests
// Using an object to create a singleton to pass to the repository
object MockRecipeService: RecipeService {
    var isSuccess = true // controls whether the mock API calls succeed or fail
    var noResults = false
    val recipes = listOf(
        Constants.Mocks.PINEAPPLE_SALAD,
        Constants.Mocks.CHOCOLATE_CUPCAKE,
        Constants.Mocks.THAI_BASIL_CHICKEN
    )

    private const val RECIPE_ERROR_STRING =
        "{\"error\":\"You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication\"}"
    val recipeError =
        RecipeError(error = "You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication")

    override suspend fun getRecipesByFilter(
        vegetarian: String?,
        vegan: String?,
        glutenFree: String?,
        healthy: String?,
        cheap: String?,
        sustainable: String?,
        spiceLevels: List<SpiceLevel>,
        mealTypes: List<MealType>,
        cuisines: List<Cuisine>,
        filters: Map<String, Any>
    ): Response<List<Recipe>> {
        return if (isSuccess) {
            if (noResults) Response.success(listOf()) else Response.success(recipes)
        } else {
            Response.error(401, RECIPE_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun getRandomRecipe(): Response<Recipe> {
        return if (isSuccess) {
            Response.success(recipes[1])
        } else {
            Response.error(401, RECIPE_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun getRecipeById(id: Int): Response<Recipe> {
        return if (isSuccess) {
            Response.success(recipes[1])
        } else {
            Response.error(401, RECIPE_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun updateRecipe(
        id: Int, fields: RecipeUpdate, token: String?
    ): Response<Token> {
        return if (isSuccess) {
            Response.success(Token())
        } else {
            Response.error(401, RECIPE_ERROR_STRING.toResponseBody())
        }
    }
}
