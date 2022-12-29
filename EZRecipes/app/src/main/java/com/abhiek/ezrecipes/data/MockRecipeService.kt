package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

// Send hardcoded recipe responses for tests
// Using an object to create a singleton to pass to the repository
object MockRecipeService: RecipeService {
    var isSuccess = true // controls whether the mock API calls succeed or fail

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

    /*
    //region Normal
    val recipe = Recipe(
        id = 635562,
        name = "Blueberry-Pineapple Salad",
        url = "https://spoonacular.com/blueberry-pineapple-salad-635562",
        image = "https://spoonacular.com/recipeImages/635562-312x231.jpg",
        credit = "Foodista.com – The Cooking Encyclopedia Everyone Can Edit",
        sourceUrl = "http://www.foodista.com/recipe/R84BP3YJ/blueberry-pineapple-salad",
        healthScore = 1,
        time = 45,
        servings = 10,
        summary = "Blueberry-Pineapple Salad might be just the side dish you are searching for. For <b>93 cents per serving</b>, this recipe <b>covers 4%</b> of your daily requirements of vitamins and minerals. One serving contains <b>297 calories</b>, <b>2g of protein</b>, and <b>13g of fat</b>. If you have cream, sugar, milk, and a few other ingredients on hand, you can make it. 1 person has made this recipe and would make it again. It is a good option if you're following a <b>gluten free</b> diet. From preparation to the plate, this recipe takes approximately <b>45 minutes</b>. All things considered, we decided this recipe <b>deserves a spoonacular score of 19%</b>. This score is not so amazing. Try <a href=\"https://spoonacular.com/recipes/kale-blueberry-pineapple-salad-672624\">Kale Blueberry Pineapple Salad</a>, <a href=\"https://spoonacular.com/recipes/blueberry-cantaloupe-and-pineapple-salad-thesaladbar-496507\">Blueberry, Cantaloupe and Pineapple Salad #TheSaladBar</a>, and <a href=\"https://spoonacular.com/recipes/chicken-strawberry-blueberry-pineapple-salad-with-poppy-seed-dressing-603148\">Chicken Strawberry Blueberry Pineapple Salad With Poppy Seed Dressing</a> for similar recipes.",
        nutrients = listOf(
            Nutrient(name = "Calories", amount = 299.3, unit = "kcal"),
            Nutrient(name = "Fat", amount = 12.58, unit = "g"),
            Nutrient(name = "Saturated Fat", amount = 6.93, unit = "g"),
            Nutrient(name = "Carbohydrates", amount = 47.28, unit = "g"),
            Nutrient(name = "Fiber", amount = 1.76, unit = "g"),
            Nutrient(name = "Sugar", amount = 43.99, unit = "g"),
            Nutrient(name = "Protein", amount = 2.58, unit = "g"),
            Nutrient(name = "Cholesterol", amount = 36.54, unit = "mg"),
            Nutrient(name = "Sodium", amount = 80.29, unit = "mg")
        ),
        ingredients = listOf(
            Ingredient(
                id = 9050,
                name = "blueberries",
                amount = 1.5,
                unit = "ounces"
            ),
            Ingredient(id = 9354, name = "canned pineapple", amount = 0.1, unit = "can"),
            Ingredient(id = 10419172, name = "cherry gelatin", amount = 0.1, unit = "large"),
            Ingredient(id = 1017, name = "cream cheese", amount = 0.8, unit = "ounces"),
            Ingredient(id = 1077, name = "milk", amount = 0.1, unit = "teaspoon"),
            Ingredient(id = 1056, name = "sour cream", amount = 0.1, unit = "cup"),
            Ingredient(id = 19335, name = "sugar", amount = 0.15, unit = "cups")
        ),
        instructions = listOf(
            Instruction(
                name = "",
                steps = listOf(
                    Step(
                        number = 1,
                        step = "Drain liquid from fruit and add water or fruit juice to make 3 cups. Make gelatin with the 3 cups heated liquid. Chill. When it starts to set, add fruit.",
                        ingredients = listOf(
                            StepItem(
                                id = 1029016,
                                name = "fruit juice",
                                image = "apple-juice.jpg"
                            ),
                            StepItem(id = 19177, name = "gelatin", image = "gelatin-powder.jpg"),
                            StepItem(id = 9431, name = "fruit", image = "mixed-fresh-fruit.jpg"),
                            StepItem(id = 14412, name = "water", image = "water.png")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 2,
                        step = "Mix ingredients for topping and spread on set salad.",
                        ingredients = listOf(StepItem(id = 0, name = "spread", image = "")),
                        equipment = listOf()
                    ),
                    Step(
                        number = 3,
                        step = "Serves 8-10",
                        ingredients = listOf(),
                        equipment = listOf()
                    )
                )
            )
        )
    )
    //endregion
    */
    //region Contains instruction name
    val recipe = Recipe(
        id = 644783,
        name = "Gluten And Dairy Free Chocolate Cupcakes",
        url = "https://spoonacular.com/gluten-and-dairy-free-chocolate-cupcakes-644783",
        image = "https://spoonacular.com/recipeImages/644783-312x231.jpg",
        credit = "Foodista.com – The Cooking Encyclopedia Everyone Can Edit",
        sourceUrl = "https://www.foodista.com/recipe/PDGXCHNP/gluten-and-dairy-free-chocolate-cupcakes",
        healthScore = 3,
        time = 45,
        servings = 4,
        summary = "The recipe Gluten And Dairy Free Chocolate Cupcakes can be made <b>in approximately about 45 minutes</b>. One serving contains <b>919 calories</b>, <b>15g of protein</b>, and <b>52g of fat</b>. This gluten free and fodmap friendly recipe serves 4 and costs <b>$2.23 per serving</b>. It works well as a budget friendly dessert. Not a lot of people made this recipe, and 1 would say it hit the spot. Head to the store and pick up cocoa, xanthan gum, baking soda, and a few other things to make it today. This recipe is typical of American cuisine. It is brought to you by Foodista. Overall, this recipe earns a <b>rather bad spoonacular score of 23%</b>. Similar recipes are <a href=\"https://spoonacular.com/recipes/gluten-free-dairy-free-chocolate-zucchini-cupcakes-557465\">Gluten Free Dairy Free Chocolate Zucchini Cupcakes</a>, <a href=\"https://spoonacular.com/recipes/grain-free-gluten-free-and-dairy-free-spiced-applesauce-cupcakes-615243\">Grain-free, Gluten-free and Dairy-free Spiced Applesauce Cupcakes</a>, and <a href=\"https://spoonacular.com/recipes/gluten-free-chocolate-cupcakes-made-with-garbanzo-bean-flour-my-best-gluten-free-cupcakes-to-date-518499\">Gluten-Free Chocolate Cupcakes Made With Garbanzo Bean Flour – My Best Gluten-Free Cupcakes To Date</a>.",
        nutrients = listOf(
            Nutrient(name = "Calories", amount = 918.45, unit = "kcal"),
            Nutrient(name = "Fat", amount = 52.47, unit = "g"),
            Nutrient(name = "Saturated Fat", amount = 31.75, unit = "g"),
            Nutrient(name = "Carbohydrates", amount = 108.17, unit = "g"),
            Nutrient(name = "Fiber", amount = 12.57, unit = "g"),
            Nutrient(name = "Sugar", amount = 74.85, unit = "g"),
            Nutrient(name = "Protein", amount = 14.53, unit = "g"),
            Nutrient(name = "Cholesterol", amount = 279.85, unit = "mg"),
            Nutrient(name = "Sodium", amount = 916.11, unit = "mg")
        ),
        ingredients = listOf(
            Ingredient(id = 93747, name = "coconut flour", amount = 0.13, unit = "cup"),
            Ingredient(id = 93696, name = "tapioca flour", amount = 0.13, unit = "cup"),
            Ingredient(id = 18372, name = "baking soda", amount = 0.25, unit = "teaspoon"),
            Ingredient(id = 2047, name = "salt", amount = 0.13, unit = "teaspoon"),
            Ingredient(id = 93626, name = "xanthan gum", amount = 0.13, unit = "teaspoon"),
            Ingredient(id = 19165, name = "cocoa", amount = 0.13, unit = "cup"),
            Ingredient(id = 14412, name = "water", amount = 0.25, unit = "cup"),
            Ingredient(id = 1123, name = "eggs", amount = 1.25, unit = ""),
            Ingredient(id = 2050, name = "vanilla", amount = 0.25, unit = "tablespoon"),
            Ingredient(id = 1001, name = "butter", amount = 2.5, unit = "tablespoons"),
            Ingredient(id = 19335, name = "sugar", amount = 0.25, unit = "cup"),
            Ingredient(
                id = 98848,
                name = "dairy free chocolate chips",
                amount = 0.25,
                unit = "cup"
            ),
            Ingredient(id = 98976, name = "coconut creamer", amount = 0.13, unit = "cup"),
            Ingredient(id = 14214, name = "instant coffee", amount = 0.5, unit = "teaspoons")
        ),
        instructions = listOf(
            Instruction(
                name = "",
                steps = listOf(
                    Step(
                        number = 1,
                        step = "Preheat oven to 375 degrees.",
                        ingredients = listOf(),
                        equipment = listOf(
                            StepItem(id = 404784, name = "oven", image = "oven.jpg")
                        )
                    ),
                    Step(
                        number = 2,
                        step = "Bring the water to a boil. Stir in the cocoa until melted and set aside until it comes to room temperature.",
                        ingredients = listOf(
                            StepItem(id = 19165, name = "cocoa powder", image = "cocoa-powder.png"),
                            StepItem(id = 14412, name = "water", image = "water.png")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 3,
                        step = "Stir together the coconut flour, cornstarch, xanthan gum, salt, and soda.",
                        ingredients = listOf(
                            StepItem(
                                id = 93747,
                                name = "coconut flour",
                                image = "coconut-flour-or-other-gluten-free-flour.jpg"
                            ),
                            StepItem(id = 93626, name = "xanthan gum", image = "white-powder.jpg"),
                            StepItem(id = 20027, name = "corn starch", image = "white-powder.jpg"),
                            StepItem(id = 2047, name = "salt", image = "salt.jpg"),
                            StepItem(id = 0, name = "pop", image = "")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 4,
                        step = "Mix together well. If you have a sifter go ahead and sift it to get out all the clumps. You don't want to bite into your cupcake and get a clump of coconut flour. I don't have a sifter so I used my hands to de-clump the flour the best I can.",
                        ingredients = listOf(
                            StepItem(
                                id = 93747,
                                name = "coconut flour",
                                image = "coconut-flour-or-other-gluten-free-flour.jpg"
                            ),
                            StepItem(id = 18139, name = "cupcakes", image = "plain-cupcake.jpg"),
                            StepItem(id = 20081, name = "all purpose flour", image = "flour.png")
                        ),
                        equipment = listOf(
                            StepItem(id = 404708, name = "sifter", image = "sifter.jpg")
                        )
                    ),
                    Step(
                        number = 5,
                        step = "Beat together the butter and sugar.",
                        ingredients = listOf(
                            StepItem(id = 1001, name = "butter", image = "butter-sliced.jpg"),
                            StepItem(id = 19335, name = "sugar", image = "sugar-in-bowl.png")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 6,
                        step = "Beat in the eggs, one at a time, then the vanilla. Scraping down the bowl as necessary.",
                        ingredients = listOf(
                            StepItem(id = 1052050, name = "vanilla", image = "vanilla.jpg"),
                            StepItem(id = 1123, name = "egg", image = "egg.png")
                        ),
                        equipment = listOf(
                            StepItem(id = 404783, name = "bowl", image = "bowl.jpg")
                        )
                    ),
                    Step(
                        number = 7,
                        step = "Add the flour mixture and beat until incorporated. Again, you might need to scrape down the bowl.",
                        ingredients = listOf(
                            StepItem(id = 20081, name = "all purpose flour", image = "flour.png")
                        ),
                        equipment = listOf(
                            StepItem(id = 404783, name = "bowl", image = "bowl.jpg")
                        )
                    ),
                    Step(
                        number = 8,
                        step = "Add in the cocoa mixture and beat until smooth. Batter will be thin.",
                        ingredients = listOf(
                            StepItem(id = 19165, name = "cocoa powder", image = "cocoa-powder.png")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 9,
                        step = "Line a muffin tin with baking cups or spray generously with oil.",
                        ingredients = listOf(
                            StepItem(id = 4582, name = "cooking oil", image = "vegetable-oil.jpg")
                        ),
                        equipment = listOf(
                            StepItem(id = 404671, name = "muffin tray", image = "muffin-tray.jpg")
                        )
                    ),
                    Step(
                        number = 10,
                        step = "Fill each cup almost to the top and bake 16-20 minutes, or until a toothpick inserted in the middle of muffin comes out clean.",
                        ingredients = listOf(),
                        equipment = listOf(
                            StepItem(id = 404644, name = "toothpicks", image = "toothpicks.jpg"),
                            StepItem(id = 404784, name = "oven", image = "oven.jpg")
                        )
                    )
                )
            ),
            Instruction(
                name = "Lets make the ganache icing",
                steps = listOf(
                    Step(
                        number = 1,
                        step = "Place chocolate chips and instant coffee in a medium sized bowl.",
                        ingredients = listOf(
                            StepItem(
                                id = 99278,
                                name = "chocolate chips",
                                image = "chocolate-chips.jpg"
                            ),
                            StepItem(
                                id = 14214,
                                name = "instant coffee",
                                image = "instant-coffee-or-instant-espresso.png"
                            )
                        ),
                        equipment = listOf(
                            StepItem(id = 404783, name = "bowl", image = "bowl.jpg")
                        )
                    ),
                    Step(
                        number = 2,
                        step = "Heat the creamer over medium heat until it reaches a gentle boil.",
                        ingredients = listOf(
                            StepItem(id = 0, name = "coffee creamer", image = "")
                        ),
                        equipment = listOf()
                    ),
                    Step(
                        number = 3,
                        step = "Pour the warm creamer over the chocolate and coffee, whisk until smooth.",
                        ingredients = listOf(
                            StepItem(id = 19081, name = "chocolate", image = "milk-chocolate.jpg"),
                            StepItem(id = 0, name = "coffee creamer", image = ""),
                            StepItem(id = 14209, name = "coffee", image = "brewed-coffee.jpg")
                        ),
                        equipment = listOf(
                            StepItem(id = 404661, name = "whisk", image = "whisk.png")
                        )
                    ),
                    Step(
                        number = 4,
                        step = "Dip the top of the cupcakes in the ganache and place in refrigerator until set- 30-60 minutes.",
                        ingredients = listOf(
                            StepItem(id = 18139, name = "cupcakes", image = "plain-cupcake.jpg"),
                            StepItem(id = 0, name = "dip", image = "")
                        ),
                        equipment = listOf()
                    )
                )
            )
        )
    )
    //endregion

    private const val recipeErrorString =
        "{\"error\":\"You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication\"}"
    val recipeError =
        RecipeError(error = "You are not authorized. Please read https://spoonacular.com/food-api/docs#Authentication")
}
