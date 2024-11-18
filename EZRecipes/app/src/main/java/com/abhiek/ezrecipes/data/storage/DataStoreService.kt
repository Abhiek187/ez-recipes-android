package com.abhiek.ezrecipes.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.data.models.TermStore
import com.abhiek.ezrecipes.utils.dataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Helper class to interface with the DataStore
 *
 * Note: DataStore stored at /data/data/PACKAGE-NAME/files/datastore
 */
class DataStoreService(context: Context) {
    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        private const val TAG = "DataStoreService"
        private val KEY_TERMS = stringPreferencesKey("terms")
        private val KEY_RECIPES_VIEWED = intPreferencesKey("recipes_viewed")
        private val KEY_LAST_VERSION_REVIEWED = intPreferencesKey("last_version_reviewed")
        private val KEY_TOKEN = byteArrayPreferencesKey("token")
    }

    suspend fun getTerms(): List<Term>? {
        val termStoreFlow = dataStore.data.map { preferences ->
            val termStoreStr = preferences[KEY_TERMS]
            val termStore = gson.fromJson(termStoreStr, TermStore::class.java) ?: return@map null

            // Replace the terms if they're expired
            if (System.currentTimeMillis() >= termStore.expireAt) {
                Log.i(TAG, "Cached terms have expired, retrieving a new set of terms...")
                return@map null
            }

            return@map termStore.terms
        }.catch { error ->
            Log.w(
                TAG,
                "Stored terms are corrupted, deleting them and retrieving a new set of terms..."
            )
            error.localizedMessage?.let { Log.w(TAG, it) }

            dataStore.edit { preferences ->
                preferences.remove(KEY_TERMS)
                emit(null)
            }
        }

        return termStoreFlow.first()
    }

    suspend fun saveTerms(terms: List<Term>) {
        dataStore.edit { preferences ->
            val termStore = TermStore(
                terms = terms,
                expireAt = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000 // 1 week
            )
            val termStoreStr = gson.toJson(termStore)
            preferences[KEY_TERMS] = termStoreStr
            Log.i(TAG, "Saved terms to DataStore!")
        }
    }

    suspend fun getRecipesViewed() = dataStore.data.map { preferences ->
        preferences[KEY_RECIPES_VIEWED] ?: 0
    }.first()

    suspend fun incrementRecipesViewed() {
        dataStore.edit { preferences ->
            val recipesViewed = preferences[KEY_RECIPES_VIEWED] ?: 0
            preferences[KEY_RECIPES_VIEWED] = recipesViewed + 1
        }
    }

    suspend fun getLastVersionReviewed() = dataStore.data.map { preferences ->
        preferences[KEY_LAST_VERSION_REVIEWED] ?: 0
    }.first()

    suspend fun setLastVersionReviewed(versionCode: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_LAST_VERSION_REVIEWED] = versionCode
        }
    }

    suspend fun getToken() = dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }.first()

    suspend fun saveToken(encryptedToken: ByteArray) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = encryptedToken
        }
    }
}
