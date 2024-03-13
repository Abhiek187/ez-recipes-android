package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeFilter
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RecipeRepositoryTest {
    private lateinit var mockService: MockRecipeService
    private lateinit var recipeRepository: RecipeRepository

    @BeforeEach
    fun setUp() {
        mockService = MockRecipeService
        recipeRepository = RecipeRepository(mockService)
    }

    @Test
    fun getRecipesByFilterSuccess() = runTest {
        // Given an instance of RecipeRepository
        // When getRecipesByFilter() is called
        mockService.isSuccess = true
        mockService.noResults = false
        val recipeFilter = RecipeFilter()
        val response = recipeRepository.getRecipesByFilter(recipeFilter)

        // Then it should return a successful response
        assertTrue(response is RecipeResult.Success)
        assertEquals((response as RecipeResult.Success).response, mockService.recipes)
    }

    @Test
    fun getRecipesByFilterNoResults() = runTest {
        // Given an instance of RecipeRepository
        // When getRecipesByFilter() is called with noResults = true
        mockService.isSuccess = true
        mockService.noResults = true
        val recipeFilter = RecipeFilter()
        val response = recipeRepository.getRecipesByFilter(recipeFilter)

        // Then the response should be empty
        assertTrue(response is RecipeResult.Success)
        assertEquals((response as RecipeResult.Success).response, listOf<Recipe>())
    }

    @Test
    fun getRecipesByFilterError() = runTest {
        // Given an instance of RecipeRepository
        // When getRecipesByFilter() is called with isSuccess = false
        mockService.isSuccess = false
        val recipeFilter = RecipeFilter()
        val response = recipeRepository.getRecipesByFilter(recipeFilter)

        // Then it should return an error
        assertTrue(response is RecipeResult.Error)
        assertEquals((response as RecipeResult.Error).recipeError, mockService.recipeError)
    }

    @Test
    fun getRandomRecipeSuccess() = runTest {
        // Given an instance of RecipeRepository
        // When the getRandomRecipe() method is called
        mockService.isSuccess = true
        val response = recipeRepository.getRandomRecipe()

        // Then it should return a successful response
        assertTrue(response is RecipeResult.Success)
        assertEquals((response as RecipeResult.Success).response, mockService.recipes[1])
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
        assertEquals((response as RecipeResult.Success).response, mockService.recipes[1])
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
