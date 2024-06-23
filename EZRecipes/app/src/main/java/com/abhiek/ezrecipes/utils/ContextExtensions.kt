package com.abhiek.ezrecipes.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Gets the underlying activity of a context
 *
 * Source: https://stackoverflow.com/a/68423182
 *
 * @return the activity, or `null` if no activity could be found
 */
tailrec fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

/**
 * DataStore singleton
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.DataStore.STORE_NAME
)
