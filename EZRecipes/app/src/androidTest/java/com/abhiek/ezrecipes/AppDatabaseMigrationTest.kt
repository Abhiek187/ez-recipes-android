package com.abhiek.ezrecipes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.Converters
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class AppDatabaseMigrationTest {
    companion object {
        private const val TEST_DB = "migration-test"
    }

    private val roomConstants = Constants.Room
    private val mockRecipes = MockRecipeService.recipes
    private val converters = Converters()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private fun insertRecipe(recipe: Recipe, db: SupportSQLiteDatabase) {
        // Safer alternative to execSQL
        val values = ContentValues()
        values.put("id", recipe.id)
        values.put("timestamp", System.currentTimeMillis())
        values.put("recipe", converters.recipeToString(recipe))

        db.insert(roomConstants.RECENT_RECIPE_TABLE, SQLiteDatabase.CONFLICT_REPLACE, values)
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() = runTest {
        // Test each migration of the Room database to make sure recipe data is retained
        migrationTestHelper.createDatabase(TEST_DB, roomConstants.VERSION_INITIAL).apply {
            // DAO classes only work on the latest schema version
            for (recipe in mockRecipes) {
                insertRecipe(recipe, this)
            }

            // Prepare for the next version.
            close()
        }

        // Validate schema changes
        migrationTestHelper.runMigrationsAndValidate(
            TEST_DB,
            roomConstants.VERSION_IS_FAVORITE,
            true
        )

        // Validate data was migrated properly
        val db = AppDatabase.getInstance(context, inMemory = true)
        // Clear any data stored from previous instrumented tests
        db.clearAllTables()
        // Close the database and release any stream resources when the test finishes
        migrationTestHelper.closeWhenFinished(db)

        val recipes = db.recentRecipeDao().getAll()

        for ((index, recipe) in recipes.withIndex()) {
            assertEquals(recipe.id, mockRecipes[index].id)
            assertFalse(recipe.isFavorite)
        }
    }
}
