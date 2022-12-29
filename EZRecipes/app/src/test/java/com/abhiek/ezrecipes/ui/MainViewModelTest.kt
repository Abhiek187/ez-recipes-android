package com.abhiek.ezrecipes.ui

import com.abhiek.ezrecipes.data.MockRecipeService
import com.abhiek.ezrecipes.data.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
internal class MainViewModelTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        mockService = MockRecipeService
        viewModel = MainViewModel(RecipeRepository(mockService))
    }

    @Test
    fun getRandomRecipeSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called
        mockService.isSuccess = true
        val fromHome = true
        viewModel.getRandomRecipe(fromHome)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipe)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertEquals(viewModel.isRecipeLoaded, fromHome)
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun getRandomRecipeError() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called with isSuccess = false
        mockService.isSuccess = false
        val fromHome = true
        viewModel.getRandomRecipe(fromHome)

        // Then the recipeError property should match the mock recipeError
        assertNull(viewModel.recipe)
        assertEquals(viewModel.recipeError, mockService.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
        assertTrue(viewModel.showRecipeAlert)
    }

    @Test
    fun getRecipeByIdSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called
        mockService.isSuccess = true
        viewModel.getRecipeById(1)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipe)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded) // fromHome is false by default
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun getRecipeByIdError() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called with isSuccess = false
        mockService.isSuccess = false
        viewModel.getRecipeById(1)

        // Then the recipeError property should match the mock recipeError
        assertNull(viewModel.recipe)
        assertEquals(viewModel.recipeError, mockService.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
        assertTrue(viewModel.showRecipeAlert)
    }
}
