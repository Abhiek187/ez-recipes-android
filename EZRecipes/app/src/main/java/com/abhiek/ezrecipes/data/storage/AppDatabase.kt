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

        /**
         * Initialize the Room database
         *
         * Note: Room stored at /data/data/PACKAGE-NAME/databases
         *
         * @param context the application context
         * @param inMemory if true, use an in-memory database
         * @return a database instance
         */
        fun getInstance(context: Context, inMemory: Boolean = false): AppDatabase {
            return if (Companion::db.isInitialized) {
                db
            } else if (inMemory) {
                db = Room.inMemoryDatabaseBuilder(
                    context, AppDatabase::class.java
                ).build()
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
