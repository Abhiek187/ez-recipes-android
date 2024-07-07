package com.abhiek.ezrecipes.ui

import android.content.Context
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
internal class MainViewModelTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var recentRecipeDao: RecentRecipeDao
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockService = MockRecipeService
        recentRecipeDao = AppDatabase.getInstance(context, inMemory = true).recentRecipeDao()
        viewModel = MainViewModel(RecipeRepository(mockService), recentRecipeDao)
    }

    @Test
    fun getRandomRecipeSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called
        mockService.isSuccess = true
        val fromHome = true
        viewModel.getRandomRecipe(fromHome)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipes[1])
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
    }

    @Test
    fun getRecipeByIdSuccess() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called
        mockService.isSuccess = true
        viewModel.getRecipeById(1)

        // Then the recipe property should match the mock recipe
        assertEquals(viewModel.recipe, mockService.recipes[1])
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
    }
}
