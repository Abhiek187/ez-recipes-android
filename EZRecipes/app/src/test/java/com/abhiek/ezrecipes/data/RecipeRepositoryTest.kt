package com.abhiek.ezrecipes.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RecipeRepositoryTest {
    private lateinit var sut: RecipeRepository // system under test

    @BeforeEach
    fun setUp() {
        sut = RecipeRepository(MockRecipeService)
    }

    @Test
    fun getRandomRecipe() = runTest {
        // Given an instance of RecipeRepository
        // When the getRandomRecipe() method is called
        // Then it should return a successful response
        val response = sut.getRandomRecipe()
        assert(response.isSuccess)
    }

    @Test
    fun getRecipeById() = runTest {
        // Given an instance of RecipeRepository
        // When the getRecipeById() method is called
        // Then it should return a successful response
        val response = sut.getRecipeById(1)
        assert(response.isSuccess)
    }
}
