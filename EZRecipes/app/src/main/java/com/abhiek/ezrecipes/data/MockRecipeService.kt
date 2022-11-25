package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.google.gson.Gson
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Send hardcoded recipe responses for tests
// Using an object to create a singleton to pass to the repository
object MockRecipeService: RecipeService {
    private const val recipeString = "{\"id\":635562,\"name\":\"Blueberry-Pineapple Salad\",\"url\":\"https://spoonacular.com/blueberry-pineapple-salad-635562\",\"image\":\"https://spoonacular.com/recipeImages/635562-312x231.jpg\",\"credit\":\"Foodista.com – The Cooking Encyclopedia Everyone Can Edit\",\"sourceUrl\":\"http://www.foodista.com/recipe/R84BP3YJ/blueberry-pineapple-salad\",\"healthScore\":1,\"time\":45,\"servings\":10,\"summary\":\"Blueberry-Pineapple Salad might be just the side dish you are searching for. For <b>93 cents per serving</b>, this recipe <b>covers 4%</b> of your daily requirements of vitamins and minerals. One serving contains <b>297 calories</b>, <b>2g of protein</b>, and <b>13g of fat</b>. If you have cream, sugar, milk, and a few other ingredients on hand, you can make it. 1 person has made this recipe and would make it again. It is a good option if you're following a <b>gluten free</b> diet. From preparation to the plate, this recipe takes approximately <b>45 minutes</b>. All things considered, we decided this recipe <b>deserves a spoonacular score of 19%</b>. This score is not so amazing. Try <a href=\\\"https://spoonacular.com/recipes/kale-blueberry-pineapple-salad-672624\\\">Kale Blueberry Pineapple Salad</a>, <a href=\\\"https://spoonacular.com/recipes/blueberry-cantaloupe-and-pineapple-salad-thesaladbar-496507\\\">Blueberry, Cantaloupe and Pineapple Salad #TheSaladBar</a>, and <a href=\\\"https://spoonacular.com/recipes/chicken-strawberry-blueberry-pineapple-salad-with-poppy-seed-dressing-603148\\\">Chicken Strawberry Blueberry Pineapple Salad With Poppy Seed Dressing</a> for similar recipes.\",\"nutrients\":[{\"name\":\"Calories\",\"amount\":299.3,\"unit\":\"kcal\"},{\"name\":\"Fat\",\"amount\":12.58,\"unit\":\"g\"},{\"name\":\"Saturated Fat\",\"amount\":6.93,\"unit\":\"g\"},{\"name\":\"Carbohydrates\",\"amount\":47.28,\"unit\":\"g\"},{\"name\":\"Fiber\",\"amount\":1.76,\"unit\":\"g\"},{\"name\":\"Sugar\",\"amount\":43.99,\"unit\":\"g\"},{\"name\":\"Protein\",\"amount\":2.58,\"unit\":\"g\"},{\"name\":\"Cholesterol\",\"amount\":36.54,\"unit\":\"mg\"},{\"name\":\"Sodium\",\"amount\":80.29,\"unit\":\"mg\"}],\"ingredients\":[{\"id\":9050,\"name\":\"blueberries\",\"amount\":1.5,\"unit\":\"ounces\"},{\"id\":9354,\"name\":\"canned pineapple\",\"amount\":0.1,\"unit\":\"can\"},{\"id\":10419172,\"name\":\"cherry gelatin\",\"amount\":0.1,\"unit\":\"large\"},{\"id\":1017,\"name\":\"cream cheese\",\"amount\":0.8,\"unit\":\"ounces\"},{\"id\":1077,\"name\":\"milk\",\"amount\":0.1,\"unit\":\"teaspoon\"},{\"id\":1056,\"name\":\"sour cream\",\"amount\":0.1,\"unit\":\"cup\"},{\"id\":19335,\"name\":\"sugar\",\"amount\":0.15,\"unit\":\"cups\"}],\"instructions\":[{\"name\":\"\",\"steps\":[{\"number\":1,\"step\":\"Drain liquid from fruit and add water or fruit juice to make 3 cups. Make gelatin with the 3 cups heated liquid. Chill. When it starts to set, add fruit.\",\"ingredients\":[{\"id\":1029016,\"name\":\"fruit juice\",\"image\":\"apple-juice.jpg\"},{\"id\":19177,\"name\":\"gelatin\",\"image\":\"gelatin-powder.jpg\"},{\"id\":9431,\"name\":\"fruit\",\"image\":\"mixed-fresh-fruit.jpg\"},{\"id\":14412,\"name\":\"water\",\"image\":\"water.png\"}],\"equipment\":[]},{\"number\":2,\"step\":\"Mix ingredients for topping and spread on set salad.\",\"ingredients\":[{\"id\":0,\"name\":\"spread\",\"image\":\"\"}],\"equipment\":[]},{\"number\":3,\"step\":\"Serves 8-10\",\"ingredients\":[],\"equipment\":[]}]}]}"
    val recipe: Recipe = Gson().fromJson(recipeString, Recipe::class.java)

    private const val recipeErrorString = "{\"error\":\"You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication\"}"
    val recipeError: RecipeError = Gson().fromJson(recipeErrorString, RecipeError::class.java)

    var isSuccess = false // controls whether the mock API calls succeed or fail

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
