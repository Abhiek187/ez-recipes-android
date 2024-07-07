package com.abhiek.ezrecipes.data.storage

import androidx.room.*
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.utils.Constants

@Dao
interface RecentRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentRecipe: RecentRecipe)

    @Query("SELECT * FROM ${Constants.Room.RECENT_RECIPE_TABLE} ORDER BY timestamp DESC")
    suspend fun getAll(): List<RecentRecipe>

    @Query("SELECT * FROM ${Constants.Room.RECENT_RECIPE_TABLE} WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecentRecipe?

    @Delete
    suspend fun delete(recentRecipe: RecentRecipe)
}
