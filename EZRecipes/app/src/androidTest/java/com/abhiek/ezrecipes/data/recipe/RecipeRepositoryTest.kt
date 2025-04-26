package com.abhiek.ezrecipes.data.recipe

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.abhiek.ezrecipes.MainDispatcherRule
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.RecentRecipeDao
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("Room 2.7.0+ fails with kotlinx.coroutines.JobCancellationException: Job was cancelled")
@RunWith(AndroidJUnit4::class)
internal class RecipeRepositoryTest {
    private lateinit var recentRecipeDao: RecentRecipeDao
    private lateinit var db: AppDatabase
    private lateinit var mockService: MockRecipeService
    private lateinit var recipeRepository: RecipeRepository

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
        db = AppDatabase.getInstance(
            context,
            inMemory = true
        )
        // Clear any data stored from previous instrumented tests
        db.clearAllTables()
        recentRecipeDao = db.recentRecipeDao()

        mockService = MockRecipeService
        recipeRepository = RecipeRepository(mockService, recentRecipeDao)
    }

    @After
    fun tearDown() {
        db.clearAllTables()
    }

    @Test
    fun fetchRecentRecipesEmpty() = runTest {
        // Given an empty database
        prepopulateDatabase(listOf())

        // When fetchRecentRecipes() is called
        val recentRecipes = recipeRepository.fetchRecentRecipes()

        // Then recentRecipes should return an empty list
        assertTrue(recentRecipes.isEmpty())
    }


    @Test
    fun fetchRecentRecipesNotEmpty() = runTest {
        // Given a database with mock recipes
        prepopulateDatabase(mockService.recipes)

        // When fetchRecentRecipes() is called
        val recentRecipes = recipeRepository.fetchRecentRecipes()

        // Then recentRecipes should return the mock recipes
        assertEquals(
            recentRecipes.map { it.recipe }.sortedBy { it.id },
            mockService.recipes.sortedBy { it.id }
        )
    }

    @Test
    fun saveNewRecentRecipe() = runTest {
        // Given a database with recipes
        prepopulateDatabase(mockService.recipes)

        // When a new recipe is added
        recipeRepository.saveRecentRecipe(mockService.recipes[0].copy(id = 2))

        // Then the number of recipes should increase by 1
        assertEquals(recentRecipeDao.getAll().size, mockService.recipes.size + 1)
    }

    @Test
    fun saveNewRecentRecipeBeyondMax() = runTest {
        // Given a database with the max number of recipes
        val recipes = List(Constants.MAX_RECENT_RECIPES) { index ->
            mockService.recipes[0].copy(id = index)
        }
        prepopulateDatabase(recipes)

        // When a new recipe is added
        recipeRepository.saveRecentRecipe(mockService.recipes[0])

        // Then the oldest recipe is deleted
        assertEquals(recentRecipeDao.getAll().size, Constants.MAX_RECENT_RECIPES)
    }

    @Test
    fun saveExistingRecentRecipe() = runTest {
        // Given a database with recipes
        prepopulateDatabase(mockService.recipes)
        val oldTimestamp = recentRecipeDao.getRecipeById(mockService.recipes[0].id)?.timestamp ?: -1

        // When an existing recipe is added
        recipeRepository.saveRecentRecipe(mockService.recipes[0])

        // Then its timestamp is updated
        val newTimestamp = recentRecipeDao.getRecipeById(mockService.recipes[0].id)?.timestamp ?: -1
        assertTrue(newTimestamp > oldTimestamp)
    }

    @Test
    fun toggleFavoriteRecentRecipe() = runTest {
        // Given a database with a favorite recipe
        val recipe = mockService.recipes[0]
        val recentRecipe = RecentRecipe(
            id = recipe.id,
            timestamp = System.currentTimeMillis(),
            recipe = recipe,
            isFavorite = true
        )
        recentRecipeDao.insert(recentRecipe)

        // When the favorite attribute is toggled
        recipeRepository.toggleFavoriteRecentRecipe(recipe.id)

        // Then the recipe's favorite status should be updated
        var newRecipe = recentRecipeDao.getRecipeById(recipe.id)
        assertNotNull(newRecipe)
        assertFalse(newRecipe!!.isFavorite)

        recipeRepository.toggleFavoriteRecentRecipe(recipe.id)
        newRecipe = recentRecipeDao.getRecipeById(recipe.id)
        assertTrue(newRecipe!!.isFavorite)
    }
}
