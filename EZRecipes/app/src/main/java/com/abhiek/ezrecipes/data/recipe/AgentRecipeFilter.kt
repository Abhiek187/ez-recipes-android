package com.abhiek.ezrecipes.data.recipe

import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionStringValueConstraint

/** Filters for searching recipes */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class AgentRecipeFilter(
    /** A full-text query to search recipes by name or description */
    var query: String? = null,
    /** The minimum number of calories for a recipe */
    var minCals: Int? = null,
    /** The maximum number of calories for a recipe */
    var maxCals: Int? = null,
    /** Whether the recipe must be vegetarian */
    var vegetarian: Boolean = false,
    /** Whether the recipe must be vegan */
    var vegan: Boolean = false,
    /** Whether the recipe must be gluten-free */
    var glutenFree: Boolean = false,
    /** Whether the recipe must be healthy */
    var healthy: Boolean = false,
    /** Whether the recipe must be cheap */
    var cheap: Boolean = false,
    /** Whether the recipe must be sustainable */
    var sustainable: Boolean = false,
    /** The minimum number of stars a recipe is rated, from 1-5 */
    var rating: Int? = null,
    /** The spice level for a recipe */
    @property:AppFunctionStringValueConstraint(enumValues = [
        "none",
        "mild",
        "spicy"
    ]) var spiceLevel: List<String> = listOf(),
    /** The meal types a recipe is appropriate for, such as breakfast, lunch, or dinner */
    @property:AppFunctionStringValueConstraint(enumValues = [
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
        "batter"
    ]) var type: List<String> = listOf(),
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
        "Scandinavian"
    ]) var culture: List<String> = listOf()
) {
    /**
     * Converts a filtered [AgentRecipeFilter] to a [RecipeFilter]
     */
    fun toRecipeFilter(): RecipeFilter {
        return RecipeFilter(
            query = this.query ?: "",
            minCals = this.minCals,
            maxCals = this.maxCals,
            vegetarian = this.vegetarian,
            vegan = this.vegan,
            glutenFree = this.glutenFree,
            healthy = this.healthy,
            cheap = this.cheap,
            sustainable = this.sustainable,
            rating = this.rating,
            spiceLevel = this.spiceLevel.map {
                SpiceLevel.valueOf(it.uppercase())
            },
            type = this.type.map {
                MealType.valueOf(
                    it.replace(" ", "_")
                        .replace("'", "_").uppercase()
                )
            },
            culture = this.culture.map {
                Cuisine.valueOf(it.replace(" ", "_").uppercase())
            }
        )
    }
}
