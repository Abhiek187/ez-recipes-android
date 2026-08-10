package com.abhiek.ezrecipes.data.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val _id: String?,
    val id: Int,
    val name: String,
    val url: String? = null,
    val image: String,
    val credit: String,
    val sourceUrl: String,
    val healthScore: Int,
    val time: Int,
    val servings: Int,
    val summary: String,
    val types: List<MealType>,
    val spiceLevel: SpiceLevel,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isHealthy: Boolean,
    val isCheap: Boolean,
    val isSustainable: Boolean,
    val culture: List<Cuisine>,
    val nutrients: List<Nutrient>,
    val ingredients: List<Ingredient>,
    val instructions: List<Instruction>,
    var token: String? = null, // searchSequenceToken for pagination
    val totalRatings: Int? = null,
    val averageRating: Double? = null,
    val views: Int? = null
) {
    /**
     * Converts a [Recipe] to an [AgentRecipe] with filtered fields
     */
    fun toAgentRecipe(): AgentRecipe {
        return AgentRecipe(
            id = this.id,
            name = this.name,
            url = this.url,
            healthScore = this.healthScore,
            time = this.time,
            servings = this.servings,
            summary = this.summary,
            types = this.types.map { it.toString() },
            spiceLevel = this.spiceLevel.toString(),
            isVegetarian = this.isVegetarian,
            isVegan = this.isVegan,
            isGlutenFree = this.isGlutenFree,
            isHealthy = this.isHealthy,
            isCheap = this.isCheap,
            isSustainable = this.isSustainable,
            culture = this.culture.map { it.toString() },
            nutrients = this.nutrients,
            ingredients = this.ingredients.map { ingredient ->
                AgentIngredient(
                    name = ingredient.name,
                    amount = ingredient.amount,
                    unit = ingredient.unit
                )
            },
            instructions = this.instructions.map { instruction ->
                AgentInstruction(
                    name = instruction.name,
                    steps = instruction.steps.map { step ->
                        AgentStep(
                            number = step.number,
                            step = step.step,
                            ingredients = step.ingredients.map { it.name },
                            equipment = step.equipment.map { it.name }
                        )
                    }
                )
            },
            totalRatings = this.totalRatings,
            averageRating = this.averageRating,
            views = this.views
        )
    }
}
