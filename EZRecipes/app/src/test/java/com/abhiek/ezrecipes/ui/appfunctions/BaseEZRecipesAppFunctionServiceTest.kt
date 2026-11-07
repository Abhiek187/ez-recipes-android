package com.abhiek.ezrecipes.ui.appfunctions

import android.util.Log
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.terms.MockTermsService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class BaseEZRecipesAppFunctionServiceTest {
    @MockK
    private lateinit var mockRecipeRepository: RecipeRepository
    @MockK
    private lateinit var mockTermsRepository: TermsRepository
    private lateinit var appFunctionService: BaseEZRecipesAppFunctionService

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
    }

    @BeforeEach
    fun setUp() {
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
        val word = "al dente"
        coEvery { mockTermsRepository.getTerms() } returns MockTermsService.terms
        // When calling the AppFunction
        val definition = appFunctionService.getRecipeDefinition(word)
        // Then it should return the definition
        val expectedDefinition = MockTermsService.terms.first {
            it.word == word
        }.definition
        assertEquals(expectedDefinition, definition)
    }
}
