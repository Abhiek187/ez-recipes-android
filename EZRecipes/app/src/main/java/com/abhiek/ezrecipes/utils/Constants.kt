package com.abhiek.ezrecipes.utils

import com.abhiek.ezrecipes.data.models.*

object Constants {
    // In Retrofit, base URLs must end with a /
    const val SERVER_BASE_URL = "https://ez-recipes-server.onrender.com"
    const val RECIPE_PATH = "/api/recipes/"
    const val TERMS_PATH = "/api/terms/"
    const val CHEFS_PATH = "/api/chefs/"
    const val TIMEOUT_SECONDS = 60L

    const val RECIPE_WEB_ORIGIN = "https://ez-recipes-web.onrender.com"

    // Error message to fallback on in case all fails
    const val UNKNOWN_ERROR = "Something went terribly wrong. Please submit a bug report to " +
            "https://github.com/Abhiek187/ez-recipes-android/issues"

    const val MIN_CALS = 0
    const val MAX_CALS = 2000
    const val PASSWORD_MIN_LENGTH = 8

    const val DATA_STORE_NAME = "data-store"
    const val MAX_RECENT_RECIPES = 10
    const val RECIPES_TO_PRESENT_REVIEW = 5

    /* Using sealedSubclasses requires reflection, which will make the app slower,
     * so list each tab manually
     */
    val TABS = listOf(Tab.Home, Tab.Search, Tab.Glossary, Tab.Profile)

    object Room {
        const val DATABASE_NAME = "AppDatabase"
        const val DATABASE_VERSION = 1
        const val RECENT_RECIPE_TABLE = "RecentRecipe"
    }

    object Mocks {
        //region Normal, no culture
        val PINEAPPLE_SALAD = Recipe(
            _id = "65dd4e615d5abcc9dc113038",
            id = 635562,
            name = "Blueberry-Pineapple Salad",
            url = "https://spoonacular.com/blueberry-pineapple-salad-635562",
            image = "https://spoonacular.com/recipeImages/635562-312x231.jpg",
            credit = "Foodista.com – The Cooking Encyclopedia Everyone Can Edit",
            sourceUrl = "http://www.foodista.com/recipe/R84BP3YJ/blueberry-pineapple-salad",
            healthScore = 1,
            time = 45,
            servings = 10,
            types = listOf(
                MealType.SIDE_DISH,
                MealType.ANTIPASTI,
                MealType.SALAD,
                MealType.STARTER,
                MealType.SNACK,
                MealType.APPETIZER,
                MealType.ANTIPASTO,
                MealType.HOR_D_OEUVRE
            ),
            spiceLevel = SpiceLevel.NONE,
            isVegetarian = false,
            isVegan = false,
            isGlutenFree = false,
            isHealthy = false,
            isCheap = false,
            isSustainable = false,
            culture = listOf(),
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
        //region Contains instruction name & culture
        val CHOCOLATE_CUPCAKE = Recipe(
            _id = "65bfe0fde939d8f4ebff712f",
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
            types = listOf(MealType.DESSERT),
            spiceLevel = SpiceLevel.NONE,
            isVegetarian = false,
            isVegan = false,
            isGlutenFree = true,
            isHealthy = false,
            isCheap = false,
            isSustainable = false,
            culture = listOf(Cuisine.AMERICAN),
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
        //region Spicy, more culture & types
        val THAI_BASIL_CHICKEN = Recipe(
            _id = "65bb6a8de939d8f4eba23cf0",
            id = 663074,
            name = "Thai Basil Chicken With Green Curry",
            url = "https://spoonacular.com/thai-basil-chicken-with-green-curry-663074",
            image = "https://spoonacular.com/recipeImages/663074-312x231.jpg",
            credit = "Foodista.com – The Cooking Encyclopedia Everyone Can Edit",
            sourceUrl = "https://www.foodista.com/recipe/7LQHVHF2/thai-basil-chicken-with-green-curry",
            healthScore = 20,
            time = 45,
            servings = 4,
            summary = "Thai Basil Chicken With Green Curry might be just the main course you are searching for. This gluten free and dairy free recipe serves 4 and costs <b>\$2.5 per serving</b>. One portion of this dish contains around <b>28g of protein</b>, <b>34g of fat</b>, and a total of <b>491 calories</b>. Only a few people made this recipe, and 1 would say it hit the spot. A mixture of chicken stock, fish sauce, curry paste, and a handful of other ingredients are all it takes to make this recipe so scrumptious. From preparation to the plate, this recipe takes roughly <b>45 minutes</b>. It is an <b>affordable</b> recipe for fans of Indian food. It is brought to you by Foodista. Taking all factors into account, this recipe <b>earns a spoonacular score of 60%</b>, which is good. Users who liked this recipe also liked <a href=\"https://spoonacular.com/recipes/thai-basil-chicken-with-green-curry-1522541\">Thai Basil Chicken With Green Curry</a>, <a href=\"https://spoonacular.com/recipes/thai-basil-chicken-with-green-curry-1531093\">Thai Basil Chicken With Green Curry</a>, and <a href=\"https://spoonacular.com/recipes/homemade-thai-green-curry-paste-and-an-easy-thai-green-curry-909057\">Homemade Thai Green Curry Paste (And An Easy Thai Green Curry)</a>.",
            types = listOf(MealType.LUNCH, MealType.MAIN_COURSE, MealType.MAIN_DISH, MealType.DINNER),
            spiceLevel = SpiceLevel.SPICY,
            isVegetarian = false,
            isVegan = false,
            isGlutenFree = true,
            isHealthy = false,
            isCheap = false,
            isSustainable = false,
            culture = listOf(Cuisine.INDIAN, Cuisine.ASIAN),
            nutrients = listOf(
                Nutrient(name = "Calories", amount = 513.83, unit = "kcal"),
                Nutrient(name = "Fat", amount = 34.05, unit = "g"),
                Nutrient(name = "Saturated Fat", amount = 22.94, unit = "g"),
                Nutrient(name = "Carbohydrates", amount = 25.12, unit = "g"),
                Nutrient(name = "Fiber", amount = 3.85, unit = "g"),
                Nutrient(name = "Sugar", amount = 19.19, unit = "g"),
                Nutrient(name = "Protein", amount = 27.88, unit = "g"),
                Nutrient(name = "Cholesterol", amount = 72.57, unit = "mg"),
                Nutrient(name = "Sodium", amount = 702.45, unit = "mg")
            ),
            ingredients = listOf(
                Ingredient(id = 4669, name = "vegetable oid", amount = 0.5, unit = "tablespoons"),
                Ingredient(id = 11282, name = "onion", amount = 0.25, unit = "medium"),
                Ingredient(id = 11821, name = "bell pepper", amount = 0.25, unit = ""),
                Ingredient(id = 11215, name = "garlic", amount = 0.5, unit = "cloves"),
                Ingredient(id = 1055062, name = "chicken breasts", amount = 0.25, unit = "pound"),
                Ingredient(id = 14106, name = "chicken stock", amount = 0.06, unit = "cup"),
                Ingredient(id = 12117, name = "coconut milk", amount = 0.25, unit = "can"),
                Ingredient(id = 6179, name = "fish sauce", amount = 0.25, unit = "tablespoon"),
                Ingredient(id = 19334, name = "brown sugar", amount = 0.5, unit = "tablespoons"),
                Ingredient(id = 10093605, name = "curry paste", amount = 0.25, unit = "tablespoon"),
                Ingredient(id = 1102047, name = "salt and pepper", amount = 1.0, unit = "servings"),
                Ingredient(id = 2044, name = "thai basil leaves", amount = 0.06, unit = "cup"),
                Ingredient(id = 10511819, name = "chilies", amount = 0.5, unit = "")
            ),
            instructions = listOf(
                Instruction(
                    name = "",
                    steps = listOf(
                        Step(
                            number = 1,
                            step = "Heat oil in a wok or large saut pan over medium high heat and stir fry the onion and pepper until slightly soft.",
                            ingredients = listOf(
                                StepItem(id = 1002030, name = "pepper", image = "pepper.jpg"),
                                StepItem(id = 11282, name = "onion", image = "brown-onion.png"),
                                StepItem(id = 4582, name = "cooking oil", image = "vegetable-oil.jpg")
                            ),
                            equipment = listOf(
                                StepItem(id = 404645, name = "frying pan", image = "pan.png"),
                                StepItem(id = 404666, name = "wok", image = "wok.png")
                            )
                        ),
                        Step(
                            number = 2,
                            step = "Add the garlic and cook for 30 more seconds.",
                            ingredients = listOf(
                                StepItem(id = 11215, name = "garlic", image = "garlic.png")
                            ),
                            equipment = listOf()
                        ),
                        Step(
                            number = 3,
                            step = "Season chicken with salt and pepper, then add to pan and cook over medium high heat, stirring occasionally and adding some oil if needed, until chicken is lightly browned.",
                            ingredients = listOf(
                                StepItem(
                                    id = 1102047,
                                    name = "salt and pepper",
                                    image = "salt-and-pepper.jpg"
                                ),
                                StepItem(id = 0, name = "chicken", image = "whole-chicken.jpg"),
                                StepItem(id = 4582, name = "cooking oil", image = "vegetable-oil.jpg")
                            ),
                            equipment = listOf(
                                StepItem(id = 404645, name = "frying pan", image = "pan.png")
                            )
                        ),
                        Step(
                            number = 4,
                            step = "Deglaze the pan with wine, then add coconut milk, fish sauce, brown sugar, green curry paste, and salt and pepper to taste.",
                            ingredients = listOf(
                                StepItem(
                                    id = 10093605,
                                    name = "green curry paste",
                                    image = "green-curry-paste.png"
                                ),
                                StepItem(
                                    id = 1102047,
                                    name = "salt and pepper",
                                    image = "salt-and-pepper.jpg"
                                ),
                                StepItem(id = 12118, name = "coconut milk", image = "coconut-milk.png"),
                                StepItem(id = 19334, name = "brown sugar", image = "dark-brown-sugar.png"),
                                StepItem(id = 6179, name = "fish sauce", image = "asian-fish-sauce.jpg"),
                                StepItem(id = 14084, name = "wine", image = "red-wine.jpg")
                            ),
                            equipment = listOf(
                                StepItem(id = 404645, name = "frying pan", image = "pan.png")
                            )
                        ),
                        Step(
                            number = 5,
                            step = "Simmer, uncovered, until sauce thickens slightly and chicken is completely cooked through, about 5 minutes.",
                            ingredients = listOf(
                                StepItem(id = 0, name = "chicken", image = "whole-chicken.jpg"),
                                StepItem(id = 0, name = "sauce", image = "")
                            ),
                            equipment = listOf()
                        ),
                        Step(
                            number = 6,
                            step = "Stir in the Thai basil leaves, then spoon into a serving bowl.",
                            ingredients = listOf(
                                StepItem(id = 2044, name = "fresh basil", image = "fresh-basil.jpg")
                            ),
                            equipment = listOf(
                                StepItem(id = 404783, name = "bowl", image = "bowl.jpg")
                            )
                        ),
                        Step(
                            number = 7,
                            step = "Garnish with red chilies if desired and serve with jasmine rice.",
                            ingredients = listOf(
                                StepItem(
                                    id = 10120444,
                                    name = "jasmine rice",
                                    image = "rice-jasmine-cooked.jpg"
                                ),
                                StepItem(
                                    id = 10511819,
                                    name = "red chili pepper",
                                    image = "red-chili.jpg"
                                )
                            ),
                            equipment = listOf()
                        )
                    )
                )
            )
        )
        //endregion
        val TERMS = listOf(
            Term(
                _id = "659355351c9a1fbc3bce6618",
                word = "produce",
                definition = "food grown by farming"
            ),
            Term(
                _id = "6593556d1c9a1fbc3bce6619",
                word = "mince",
                definition = "cut up into small pieces"
            ),
            Term(
                _id = "659355831c9a1fbc3bce661a",
                word = "broil",
                definition = "cook, such as in an oven"
            ),
            Term(
                _id = "659355951c9a1fbc3bce661b",
                word = "simmer",
                definition = "stay below the boiling point when heated, such as with water"
            ),
            Term(
                _id = "659355a41c9a1fbc3bce661c",
                word = "al dente",
                definition = "(\"to the tooth\") pasta or rice that's cooked so it can be chewed"
            )
        )
        val CHEF = Chef(
            uid = "oJG5PZ8KIIfvQMDsQzOwDbu2m6O2",
            email = "test@email.com",
            emailVerified = true,
            ratings = mapOf(
                "641024" to 5,
                "663849" to 3
            ),
            recentRecipes = mapOf(
                "641024" to "2024-10-17T02:54:07.471+00:00",
                "663849" to "2024-10-17T22:28:27.387+00:00"
            ),
            favoriteRecipes = listOf("641024"),
            token = "e30.e30.e30"
        )
    }
}
