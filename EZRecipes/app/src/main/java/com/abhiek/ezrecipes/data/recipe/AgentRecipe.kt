package com.abhiek.ezrecipes.data.recipe

import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionStringValueConstraint

/** Recipe details, optimized for LLMs */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentRecipe(
    /** Unique ID for the recipe */
    val id: Int,
    /** Recipe name */
    val name: String,
    /** Source URL of the recipe, if provided */
    val url: String?,
    /** The health score for a recipe as a percentage */
    val healthScore: Int,
    /** The time in minutes to make the recipe */
    val time: Int,
    /** The number of servings a recipe provides */
    val servings: Int,
    /** Paragraph summarizing the recipe */
    val summary: String,
    /** The meal types a recipe is appropriate for, such as breakfast, lunch, or dinner */
    @property:AppFunctionStringValueConstraint(enumValues = [
        // AppFunctions don't support enums
        "main course",
        "side dish",
        "dessert",
        "appetizer",
        "salad",
        "bread",
        "breakfast",
        "soup",
        "beverage",
        "sauce",
        "marinade",
        "fingerfood",
        "snack",
        "drink",
        "antipasti",
        "starter",
        "antipasto",
        "hor d'oeuvre",
        "lunch",
        "main dish",
        "dinner",
        "morning meal",
        "brunch",
        "condiment",
        "dip",
        "spread",
        "smoothie",
        "cocktail",
        "mocktail",
        "seasoning",
        "batter",
        "unknown"
    ]) val types: List<String>,
    /** The spice level for a recipe */
    @property:AppFunctionStringValueConstraint(enumValues = [
        "none",
        "mild",
        "spicy",
        "unknown"
    ]) val spiceLevel: String,
    /** Whether the recipe is vegetarian */
    val isVegetarian: Boolean,
    /** Whether the recipe is vegan */
    val isVegan: Boolean,
    /** Whether the recipe is gluten-free */
    val isGlutenFree: Boolean,
    /** Whether the recipe is healthy */
    val isHealthy: Boolean,
    /** Whether the recipe is cheap */
    val isCheap: Boolean,
    /** Whether the recipe is sustainable */
    val isSustainable: Boolean,
    /** The cuisine types associated with a recipe, such as American, Italian, or Latin American */
    @property:AppFunctionStringValueConstraint(enumValues = [
        "African",
        "Asian",
        "American",
        "British",
        "Cajun",
        "Caribbean",
        "Chinese",
        "Eastern European",
        "European",
        "French",
        "German",
        "Greek",
        "Indian",
        "Irish",
        "Italian",
        "Japanese",
        "Jewish",
        "Korean",
        "Latin American",
        "Mediterranean",
        "Mexican",
        "Middle Eastern",
        "Nordic",
        "Southern",
        "Spanish",
        "Thai",
        "Vietnamese",
        "English",
        "Scottish",
        "South American",
        "Creole",
        "Central American",
        "bbq",
        "Barbecue",
        "Scandinavian",
        "Unknown"
    ]) val culture: List<String>,
    /** Nutritional information about a recipe */
    val nutrients: List<Nutrient>,
    /** Ingredients to make a recipe */
    val ingredients: List<AgentIngredient>,
    /** Recipe instructions */
    val instructions: List<AgentInstruction>,
    /** The total number of ratings provided for a recipe */
    val totalRatings: Int?,
    /** The average rating out of 5 for a recipe */
    val averageRating: Double?,
    /** The number of times the recipe has been viewed */
    val views: Int?
)

/** A recipe ingredient */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentIngredient(
    /** Ingredient name */
    val name: String,
    /** Numerical amount of the ingredient */
    val amount: Double,
    /** Units to measure each ingredient */
    val unit: String
)

/** Recipe instructions */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentInstruction(
    /** Instruction title */
    val name: String,
    /** A list of steps per instruction */
    val steps: List<AgentStep>
)

/** Recipe steps */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentStep(
    /** Step number */
    val number: Int,
    /** Step description */
    val step: String,
    /** Ingredients required for the step */
    val ingredients: List<String>,
    /** Equipment required for the step */
    val equipment: List<String>
)
