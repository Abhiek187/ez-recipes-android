package com.abhiek.ezrecipes.ui.profile

import android.content.Context
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.ClearCredentialException
import com.abhiek.ezrecipes.data.chef.ExistingPasskeyClientResponse
import com.abhiek.ezrecipes.data.chef.NewPasskeyClientResponse
import com.abhiek.ezrecipes.data.chef.PasskeyCreationOptions
import com.abhiek.ezrecipes.data.chef.PasskeyRequestOptions
import kotlinx.serialization.json.Json

/**
 * Helper class to manage restore keys using the Credential Manager
 *
 * Minimum Google Play Services version required: 24.22.00 (24220000)
 *
 * Check by running: `adb shell pm dump com.google.android.gms | grep version`
 */
class RestoreKeyManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "RestoreKeyManager"
    }

    suspend fun getRestoreKey(
        // Reusing passkey types since restore keys follow the same standard
        serverPasskeyOptions: PasskeyRequestOptions
    ): ExistingPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val restoreOptions = GetRestoreCredentialOption(
            Json.encodeToString(serverPasskeyOptions)
        )
        val getRestoreRequest = GetCredentialRequest(
            listOf(restoreOptions)
        )
        // Pre-warming isn't needed here since restore keys are handled in the background
        val restoreCredential = credentialManager.getCredential(
            context,
            getRestoreRequest
        ).credential as RestoreCredential

        // Convert the Credential Manager response to a standard WebAuthn response
        return Json.decodeFromString(restoreCredential.authenticationResponseJson)
    }

    suspend fun createRestoreKey(
        serverPasskeyOptions: PasskeyCreationOptions
    ): NewPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val createRestoreRequest = CreateRestoreCredentialRequest(
            Json.encodeToString(serverPasskeyOptions)
        )
        val createRestoreResponse = credentialManager.createCredential(
            context,
            createRestoreRequest
        ) as CreateRestoreCredentialResponse

        // Convert the Credential Manager response to a standard WebAuthn response
        return Json.decodeFromString(createRestoreResponse.responseJson)
    }

    suspend fun deleteRestoreKey() {
        try {
            val clearRequest = ClearCredentialStateRequest(
                ClearCredentialStateRequest.TYPE_CLEAR_RESTORE_CREDENTIAL
            )
            credentialManager.clearCredentialState(clearRequest)
            Log.d(TAG, "Deleted the restore key")
        } catch (ex: ClearCredentialException) {
            Log.w(TAG, "Failed to delete the restore key :: error: ${ex.localizedMessage}")
        }
    }
}
