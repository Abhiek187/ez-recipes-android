package com.abhiek.ezrecipes.ui.appfunctions

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.*
import com.abhiek.ezrecipes.data.terms.MockTermsService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class BaseEZRecipesAppFunctionServiceTest {
    private lateinit var mockRecipeRepository: RecipeRepository
    private lateinit var mockTermsRepository: TermsRepository
    private lateinit var appFunctionService: BaseEZRecipesAppFunctionService

    private val testFilter = AgentRecipeFilter(
        query = "pizza",
        minCals = 500,
        maxCals = 800,
        vegetarian = true,
        vegan = false,
        glutenFree = true,
        healthy = false,
        cheap = false,
        sustainable = false,
        rating = 4,
        spiceLevel = listOf(SpiceLevel.MILD.name, SpiceLevel.SPICY.name),
        type = listOf(MealType.LUNCH.name, MealType.DINNER.name),
        culture = listOf(Cuisine.INDIAN.name)
    )
    private val testRecipes = MockRecipeService.recipes
    private val testRecipeError = MockRecipeService.recipeError
    private val testWord = "al dente"
    private val testDefinition = "(\"to the tooth\") pasta or rice that's cooked so it can be chewed"

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
    }

    @BeforeEach
    fun setUp() {
        mockRecipeRepository = mockkClass(RecipeRepository::class) {
            coEvery { getRecipesByFilter(any()) } returns RecipeResult.Success(testRecipes)
            coEvery { getRecipeById(any()) } returns RecipeResult.Success(testRecipes[1])
        }
        mockTermsRepository = mockkClass(TermsRepository::class) {
            coEvery { getTerms() } returns MockTermsService.terms
        }

        appFunctionService = MockEZRecipesAppFunctionService()
        appFunctionService.recipeRepository = mockRecipeRepository
        appFunctionService.termsRepository = mockTermsRepository

        mockLog()
    }

    @Test
    fun searchRecipesSuccess() = runTest {
        // Given a valid filter
        // When calling the AppFunction
        val recipes = appFunctionService.searchRecipes(testFilter)
        // Then it should return a list of recipes
        val expectedRecipes = testRecipes.map { recipe ->
            recipe.toAgentRecipePreview()
        }
        assertEquals(expectedRecipes, recipes)
    }

    @Test
    fun searchRecipesNoFilter() = runTest {
        // Given an empty filter
        // When calling the AppFunction
        // Then it should throw an exception
        // Unable to initialize or mock the AppFunction exceptions due to calling Bundle
        // So check for a generic exception instead
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.searchRecipes(AgentRecipeFilter())
        }
        // assertEquals("At least one filter must be provided", exception.message)
    }

    @Test
    fun searchRecipesBlankQuery() = runTest {
        // Given a blank query
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.searchRecipes(AgentRecipeFilter(query = " "))
        }
        // assertEquals("Query cannot be blank", exception.message)
    }

    @Test
    fun searchRecipesInvalidMinCals() = runTest {
        // Given an invalid min calories filter
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.searchRecipes(AgentRecipeFilter(minCals = -1))
        }
        // assertEquals("Minimum calories must be between ${Constants.MIN_CALS} and ${Constants.MAX_CALS}", exception.message)
    }

    @Test
    fun searchRecipesInvalidMaxCals() = runTest {
        // Given an invalid max calories filter
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.searchRecipes(AgentRecipeFilter(maxCals = 99999))
        }
        // assertEquals("Maximum calories must be between ${Constants.MIN_CALS} and ${Constants.MAX_CALS}", exception.message)
    }

    @Test
    fun searchRecipesInvalidRating() = runTest {
        // Given an invalid rating
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.searchRecipes(AgentRecipeFilter(rating = 0))
        }
        // assertEquals("Rating must be between 1 and 5", exception.message)
    }

    @Test
    fun searchRecipesInvalidSpiceLevel() = runTest {
        // Given an invalid spice level
        // When calling the AppFunction
        // Then it should throw an exception
        // An AppFunction exception won't be thrown, but toRecipeFilter() will throw an error
        val exception = assertThrows<IllegalArgumentException> {
            appFunctionService.searchRecipes(
                AgentRecipeFilter(
                    spiceLevel = listOf("hot")
                )
            )
        }
        assertEquals(true, exception.message?.startsWith("No enum constant"))
    }

    @Test
    fun searchRecipesInvalidMealType() = runTest {
        // Given an invalid meal type
        // When calling the AppFunction
        // Then it should throw an exception
        val exception = assertThrows<IllegalArgumentException> {
            appFunctionService.searchRecipes(AgentRecipeFilter(
                type = listOf("midnight snack")
            ))
        }
        assertEquals(true, exception.message?.startsWith("No enum constant"))
    }

    @Test
    fun searchRecipesInvalidCuisine() = runTest {
        // Given an invalid cuisine
        // When calling the AppFunction
        // Then it should throw an exception
        val exception = assertThrows<IllegalArgumentException> {
            appFunctionService.searchRecipes(AgentRecipeFilter(
                culture = listOf("Klingon")
            ))
        }
        assertEquals(true, exception.message?.startsWith("No enum constant"))
    }

    @Test
    fun searchRecipesError() = runTest {
        // Given a valid filter
        // When calling the AppFunction and an error occurs
        coEvery { mockRecipeRepository.getRecipesByFilter(any()) } returns RecipeResult.Error(testRecipeError)
        // Then it should throw an exception
        assertThrows</*AppFunctionAppUnknown*/Exception> {
            appFunctionService.searchRecipes(testFilter)
        }
        // assertEquals("Failed to search recipes :: ${testRecipeError.recipeError}", exception.message)
    }

    @Test
    fun getRecipeDetailsSuccess() = runTest {
        // Given a valid recipe ID
        // When calling the AppFunction
        val recipe = appFunctionService.getRecipeDetails(644783)
        // Then it should return the recipe
        assertEquals(testRecipes[1].toAgentRecipe(), recipe)
    }

    @Test
    fun getRecipeDetailsInvalidRecipe() = runTest {
        // Given an invalid recipe ID
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.getRecipeDetails(-1)
        }
        // assertEquals("Recipe ID cannot be negative", exception.message)
    }

    @Test
    fun getRecipeDetailsUnknownRecipe() = runTest {
        // Given an unknown recipe ID
        val id = 6447831
        // When calling the AppFunction
        coEvery { mockRecipeRepository.getRecipeById(id) } returns RecipeResult.Error(testRecipeError)
        // Then it should throw an exception
        assertThrows</*AppFunctionElementNotFound*/Exception> {
            appFunctionService.getRecipeDetails(id)
        }
        // assertEquals("Recipe with ID $id could not be found", exception.message)
    }

    @Test
    fun getRecipeDefinitionSuccess() = runTest {
        // Given a valid word
        // When calling the AppFunction
        val definition = appFunctionService.getRecipeDefinition(testWord)
        // Then it should return the definition
        assertEquals(testDefinition, definition)
    }

    @Test
    fun getRecipeDefinitionCaseInsensitive() = runTest {
        // Given a valid word with different casing
        // When calling the AppFunction
        val definition = appFunctionService.getRecipeDefinition("AL Dente")
        // Then it should return the definition
        assertEquals(testDefinition, definition)
    }

    @Test
    fun getRecipeDefinitionBlankWord() = runTest {
        // Given a blank word
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionInvalidArgument*/Exception> {
            appFunctionService.getRecipeDefinition("")
        }
        // assertEquals("Word cannot be blank", exception.message)
    }

    @Test
    fun getRecipeDefinitionUnknownWord() = runTest {
        // Given an unknown word
        val word = "pizza"
        // When calling the AppFunction
        // Then it should throw an exception
        assertThrows</*AppFunctionElementNotFound*/Exception> {
            appFunctionService.getRecipeDefinition(word)
        }
        // assertEquals("No definition found for word $word", exception.message)
    }
}
