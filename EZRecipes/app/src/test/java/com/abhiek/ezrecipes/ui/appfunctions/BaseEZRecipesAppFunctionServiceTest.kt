package com.abhiek.ezrecipes.ui.appfunctions

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
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
        mockRecipeRepository = mockkClass(RecipeRepository::class)
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
    }

    @Test
    fun getRecipeDetailsSuccess() = runTest {
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
