package com.abhiek.ezrecipes.data.recipe

import com.abhiek.ezrecipes.utils.Constants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RecipeTest {
    @Test
    fun toAgentRecipe() {
        // Given a recipe
        val recipe = Constants.Mocks.CHOCOLATE_CUPCAKE
        // When converted into an agent recipe
        val agentRecipe = recipe.toAgentRecipe()
        // Then it should map the correct fields
        assertEquals(agentRecipe, AgentRecipe(
            id = recipe.id,
            name = recipe.name,
            url = recipe.url,
            healthScore = recipe.healthScore,
            time = recipe.time,
            servings = recipe.servings,
            summary = recipe.summary,
            types = recipe.types.map { it.toString() },
            spiceLevel = recipe.spiceLevel.toString(),
            isVegetarian = recipe.isVegetarian,
            isVegan = recipe.isVegan,
            isGlutenFree = recipe.isGlutenFree,
            isHealthy = recipe.isHealthy,
            isCheap = recipe.isCheap,
            isSustainable = recipe.isSustainable,
            culture = recipe.culture.map { it.toString() },
            nutrients = recipe.nutrients,
            ingredients = recipe.ingredients.map { ingredient ->
                AgentIngredient(
                    name = ingredient.name,
                    amount = ingredient.amount,
                    unit = ingredient.unit
                )
            },
            instructions = recipe.instructions.map { instruction ->
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
            totalRatings = recipe.totalRatings,
            averageRating = recipe.averageRating,
            views = recipe.views
        ))
    }
}
