package com.abhiek.ezrecipes.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhiek.ezrecipes.utils.Constants

@Entity(tableName = Constants.Room.RECENT_RECIPE_TABLE)
data class RecentRecipe(
    @PrimaryKey val id: Int, // extract id from recipe to detect duplicates
    var timestamp: Long,
    var recipe: Recipe,
    @ColumnInfo(defaultValue = "0") var isFavorite: Boolean = false // 0 = false, 1 = true
)
