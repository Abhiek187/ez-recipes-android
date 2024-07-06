package com.abhiek.ezrecipes.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.utils.Constants

@Dao
interface RecentRecipeDao {
    @Query("SELECT * FROM ${Constants.Room.RECENT_RECIPE_TABLE} ORDER BY timestamp DESC")
    suspend fun getAll(): List<RecentRecipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentRecipe: RecentRecipe)
}
