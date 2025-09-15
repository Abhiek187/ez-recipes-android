package com.abhiek.ezrecipes.ui.search

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
internal class SearchViewModelTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var viewModel: SearchViewModel

    @BeforeEach
    fun setUp() {
        mockService = MockRecipeService
        viewModel = SearchViewModel(RecipeRepository(mockService))
    }

    @Test
    fun searchRecipesSuccess() = runTest {
        // Given an instance of SearchViewModel
        // When searchRecipes() is called
        mockService.isSuccess = true
        mockService.noResults = false
        viewModel.searchRecipes()

        // Then the recipes property should match the mock recipes
        assertEquals(viewModel.recipes, mockService.recipes)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
        assertFalse(viewModel.noRecipesFound)
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun searchRecipesSuccessFromFilterForm() = runTest {
        // Given an instance of SearchViewModel
        // When searchRecipes() is called from the filter form
        mockService.isSuccess = true
        mockService.noResults = false
        viewModel.searchRecipes(fromFilterForm = true)

        // Then isRecipeLoaded should be true
        assertTrue(viewModel.isRecipeLoaded)
    }

    @Test
    fun searchRecipesNoResults() = runTest {
        // Given an instance of SearchViewModel
        // When searchRecipes() is called with noResults = true
        mockService.isSuccess = true
        mockService.noResults = true
        viewModel.searchRecipes()

        // Then the recipes property should be empty
        assertEquals(viewModel.recipes, listOf<Recipe>())
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
        assertTrue(viewModel.noRecipesFound)
        assertFalse(viewModel.showRecipeAlert)
    }

    @Test
    fun searchRecipesError() = runTest {
        // Given an instance of SearchViewModel
        // When searchRecipes() is called with isSuccess = false
        mockService.isSuccess = false
        viewModel.searchRecipes()

        // Then the recipeError property should match the mock recipeError
        assertEquals(viewModel.recipes, listOf<Recipe>())
        assertEquals(viewModel.recipeError, mockService.recipeError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isRecipeLoaded)
        assertFalse(viewModel.noRecipesFound)
    }
}
