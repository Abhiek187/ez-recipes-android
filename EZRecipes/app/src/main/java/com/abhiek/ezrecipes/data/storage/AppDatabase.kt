package com.abhiek.ezrecipes.data.storage

import android.content.Context
import androidx.room.*
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@Database(
    entities = [RecentRecipe::class],
    version = Constants.Room.VERSION_IS_FAVORITE,
    autoMigrations = [
        // Add isFavorite column
        AutoMigration(
            from = Constants.Room.VERSION_INITIAL,
            to = Constants.Room.VERSION_IS_FAVORITE
        )
    ]
)
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
         * @param dispatcher the coroutine dispatcher to use
         * @return a database instance
         */
        fun getInstance(
            context: Context,
            inMemory: Boolean = false,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): AppDatabase {
            return if (Companion::db.isInitialized) {
                db
            } else if (inMemory) {
                db = Room.inMemoryDatabaseBuilder(
                    context, AppDatabase::class.java
                ).setQueryExecutor(dispatcher.asExecutor())
                    .setTransactionExecutor(dispatcher.asExecutor())
                    .build()
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
