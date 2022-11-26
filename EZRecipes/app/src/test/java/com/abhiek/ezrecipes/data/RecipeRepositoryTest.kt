package com.abhiek.ezrecipes.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RecipeRepositoryTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var recipeRepository: RecipeRepository

    @BeforeEach
    fun setUp() {
        mockService = MockRecipeService
        recipeRepository = RecipeRepository(mockService)
    }

    @Test
    fun getRandomRecipeSuccess() = runTest {
        // Given an instance of RecipeRepository
        // When the getRandomRecipe() method is called
        mockService.isSuccess = true
        val response = recipeRepository.getRandomRecipe()

        // Then it should return a successful response
        assertTrue(response is RecipeResult.Success)
        assertEquals((response as RecipeResult.Success).recipe, mockService.recipe)
    }

    @Test
    fun getRandomRecipeError() = runTest {
        // Given an instance of RecipeRepository
        // When the getRandomRecipe() method is called with isSuccess = false
        mockService.isSuccess = false
        val response = recipeRepository.getRandomRecipe()

        // Then it should return an error
        assertTrue(response is RecipeResult.Error)
        assertEquals((response as RecipeResult.Error).recipeError, mockService.recipeError)
    }

    @Test
    fun getRecipeByIdSuccess() = runTest {
        // Given an instance of RecipeRepository
        // When the getRecipeById() method is called
        mockService.isSuccess = true
        val response = recipeRepository.getRecipeById(1)

        // Then it should return a successful response
        assertTrue(response is RecipeResult.Success)
        assertEquals((response as RecipeResult.Success).recipe, mockService.recipe)
    }

    @Test
    fun getRecipeByIdError() = runTest {
        // Given an instance of RecipeRepository
        // When the getRecipeById() method is called with isSuccess = false
        mockService.isSuccess = false
        val response = recipeRepository.getRecipeById(1)

        // Then it should return an error response
        assertTrue(response is RecipeResult.Error)
        assertEquals((response as RecipeResult.Error).recipeError, mockService.recipeError)
    }
}
