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
@ExtendWith(InstantExecutorExtension::class, MainDispatcherExtension::class)
internal class MainViewModelTest {
    private lateinit var sut: MainViewModel

    @BeforeEach
    fun setUp() {
        sut = MainViewModel(RecipeRepository(MockRecipeService))
    }

    @Test
    fun getRandomRecipe() = runTest {
        // Given an instance of MainViewModel
        // When the getRandomRecipe() method is called
        // Then the recipe property should match the mock recipe
        sut.getRandomRecipe()
        assertEquals(sut.recipe.value, MockRecipeService.recipe)
    }

    @Test
    fun getRecipeById() = runTest {
        // Given an instance of MainViewModel
        // When the getRecipeById() method is called
        // Then the recipe property should match the mock recipe
        sut.getRecipeById("1")
        assertEquals(sut.recipe.value, MockRecipeService.recipe)
    }
}
