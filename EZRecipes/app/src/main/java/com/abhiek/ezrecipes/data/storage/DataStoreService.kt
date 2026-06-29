package com.abhiek.ezrecipes.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abhiek.ezrecipes.data.chef.RememberMe
import com.abhiek.ezrecipes.data.terms.Term
import com.abhiek.ezrecipes.data.terms.TermStore
import com.abhiek.ezrecipes.utils.dataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Helper class to interface with the DataStore
 *
 * Note: DataStore stored at /data/data/PACKAGE-NAME/files/datastore
 */
class DataStoreService(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private const val TAG = "DataStoreService"
        private val KEY_TERMS = stringPreferencesKey("terms")
        private val KEY_RECIPES_VIEWED = intPreferencesKey("recipes_viewed")
        private val KEY_LAST_VERSION_REVIEWED = intPreferencesKey("last_version_reviewed")
        private val KEY_TOKEN = byteArrayPreferencesKey("token")
        private val KEY_REMEMBER_ME = stringPreferencesKey("remember_me")
    }

    suspend fun getTerms(): List<Term>? {
        val termStoreFlow = dataStore.data.map { preferences ->
            val termStoreStr = preferences[KEY_TERMS] ?: return@map null
            val termStore = Json.decodeFromString<TermStore>(termStoreStr)

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
                expireAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000 // 1 week
            )
            val termStoreStr = Json.encodeToString(termStore)
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

    suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
    }

    suspend fun getUsername(): String? {
        val rememberMeFlow = dataStore.data.map { preferences ->
            val rememberMeStr = preferences[KEY_REMEMBER_ME] ?: return@map null
            val rememberMe = Json.decodeFromString<RememberMe>(rememberMeStr)

            // Delete the username if it's expired
            if (System.currentTimeMillis() >= rememberMe.expireAt) {
                Log.i(TAG, "Remembered username has expired")
                clearUsername()
                return@map null
            }

            return@map rememberMe.username
        }.catch { error ->
            Log.w(TAG, "Remember Me is corrupted, clearing the entry...")
            error.localizedMessage?.let { Log.w(TAG, it) }

            clearUsername()
            emit(null)
        }

        return rememberMeFlow.first()
    }

    suspend fun saveUsername(username: String? = null) {
        dataStore.edit { preferences ->
            val newUsername = if (username != null) {
                username
            } else {
                // If no username is provided, use the existing username saved
                val existingUsername = getUsername()
                if (existingUsername != null) {
                    existingUsername
                } else {
                    // If no username is saved, don't do anything
                    Log.w(TAG, "No username provided and no username saved, returning early")
                    return@edit
                }
            }

            val rememberMe = RememberMe(
                username = newUsername,
                // L is needed to prevent the right side from overflowing
                expireAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000 // 1 month
            )
            val rememberMeStr = Json.encodeToString(rememberMe)
            preferences[KEY_REMEMBER_ME] = rememberMeStr
            Log.i(TAG, "Saved username $newUsername to DataStore!")
        }
    }

    suspend fun clearUsername() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_REMEMBER_ME)
        }
    }
}
