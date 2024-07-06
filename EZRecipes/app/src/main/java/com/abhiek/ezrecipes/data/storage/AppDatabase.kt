package com.abhiek.ezrecipes.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.utils.Constants

@Database(entities = [RecentRecipe::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recentRecipeDao(): RecentRecipeDao

    companion object {
        private lateinit var db: AppDatabase

        // Initialize the Room database when first referencing the singleton
        // Room stored at /data/data/PACKAGE-NAME/database
        fun getInstance(context: Context): AppDatabase {
            return if (Companion::db.isInitialized) {
                db
            } else {
                db = Room.databaseBuilder(
                    context, AppDatabase::class.java, Constants.Room.DATABASE_NAME
                ).build()
                db
            }
        }
    }
}
