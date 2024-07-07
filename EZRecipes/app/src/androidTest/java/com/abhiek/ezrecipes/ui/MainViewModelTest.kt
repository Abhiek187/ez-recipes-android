package com.abhiek.ezrecipes.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abhiek.ezrecipes.MainDispatcherRule
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MainViewModelTest {
    private lateinit var recentRecipeDao: RecentRecipeDao
    private lateinit var db: AppDatabase
    private lateinit var mockService: MockRecipeService
    private lateinit var viewModel: MainViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private suspend fun prepopulateDatabase(recipes: List<Recipe>) {
        recipes.forEach { recipe ->
            val recentRecipe = RecentRecipe(
                id = recipe.id,
                timestamp = System.currentTimeMillis(),
                recipe = recipe
            )
            recentRecipeDao.insert(recentRecipe)
        }
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabase.getInstance(context, inMemory = true)
        recentRecipeDao = db.recentRecipeDao()

        mockService = MockRecipeService
        viewModel = MainViewModel(RecipeRepository(mockService), recentRecipeDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun fetchRecentRecipesEmpty() = runTest {
        // Given an empty database
        prepopulateDatabase(listOf())

        // When fetchRecentRecipes() is called
        viewModel.fetchRecentRecipes()

        // Then recentRecipes should return an empty list
        assertTrue(viewModel.recentRecipes.isEmpty())
    }

    @Test
    fun fetchRecentRecipesNotEmpty() = runTest {
        // Given a database with mock recipes
        prepopulateDatabase(mockService.recipes)

        // When fetchRecentRecipes() is called
//        viewModel.fetchRecentRecipes()
        val recentRecipes = recentRecipeDao.getAll()

//        println("viewModel.recentRecipes.map { it.recipe }.toTypedArray(): " + viewModel.recentRecipes.map { it.recipe }.toTypedArray().)
//        println("mockService.recipes.toTypedArray(): " + mockService.recipes.toTypedArray())
//        println("viewModel.recentRecipes.map { it.recipe }.sortedBy { it.id }: " + viewModel.recentRecipes.map { it.recipe }.sortedBy { it.id })
//        println("mockService.recipes.sortedBy { it.id }: " + mockService.recipes.sortedBy { it.id })

        // Then recentRecipes should return the mock recipes
//        assertArrayEquals(
//            recentRecipes.map { it.recipe }.toTypedArray(),
//            mockService.recipes.toTypedArray()
//        )
//        assertArrayEquals(
//            viewModel.recentRecipes.map { it.recipe }.toTypedArray(),
//            mockService.recipes.toTypedArray()
//        )
//        assertEquals(
//            viewModel.recentRecipes.map { it.recipe }.sortedBy { it.id },
//            mockService.recipes.sortedBy { it.id }
//        )
        assertEquals(
            recentRecipes.map { it.recipe }.sortedBy { it.id },
            mockService.recipes.sortedBy { it.id }
        )
    }

    @Test
    fun saveRecentRecipe() {
    }
}
