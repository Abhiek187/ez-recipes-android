package com.abhiek.ezrecipes.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.*
import androidx.credentials.exceptions.publickeycredential.SignalCredentialStateException
import com.abhiek.ezrecipes.data.chef.ExistingPasskeyClientResponse
import com.abhiek.ezrecipes.data.chef.NewPasskeyClientResponse
import com.abhiek.ezrecipes.data.chef.PasskeyCreationOptions
import com.abhiek.ezrecipes.data.chef.PasskeyRequestOptions
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.base64UrlEncode
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

/**
 * Helper class to manage passkey creation and retrieval using the Credential Manager
 *
 * Minimum Google Play Services version required: 23.08.15 (230815045)
 *
 * Check by running: `adb shell pm dump com.google.android.gms | grep version`
 */
class PasskeyManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "PasskeyManager"
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getPasskey(
        serverPasskeyOptions: PasskeyRequestOptions
    ): ExistingPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val androidPasskeyOptions = GetPublicKeyCredentialOption(
            Json.encodeToString(serverPasskeyOptions)
        )
        val androidPasskeyRequest = GetCredentialRequest(
            listOf(androidPasskeyOptions)
        )
        // Triggers the device to prompt for a passkey
        val androidPasskeyResponse = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        ) {
            // Pre-warm the sign-in request
            val pendingAndroidPasskeyHandle = credentialManager.prepareGetCredential(
                androidPasskeyRequest
            ).pendingGetCredentialHandle

            if (pendingAndroidPasskeyHandle != null) {
                credentialManager.getCredential(
                    context,
                    pendingAndroidPasskeyHandle
                ).credential as PublicKeyCredential
            } else {
                credentialManager.getCredential(
                    context,
                    androidPasskeyRequest
                ).credential as PublicKeyCredential
            }
        } else {
            credentialManager.getCredential(
                context,
                androidPasskeyRequest
            ).credential as PublicKeyCredential
        }

        // Convert the Credential Manager response to a standard WebAuthn response
        return Json.decodeFromString(androidPasskeyResponse.authenticationResponseJson)
    }

    @SuppressLint("PublicKeyCredential")
    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun createPasskey(
        serverPasskeyOptions: PasskeyCreationOptions
    ): NewPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val androidPasskeyRequest = CreatePublicKeyCredentialRequest(
            Json.encodeToString(serverPasskeyOptions)
        )
        // Triggers the device to prompt for a passkey
        val androidPasskeyResponse = credentialManager.createCredential(
            context,
            androidPasskeyRequest
        ) as CreatePublicKeyCredentialResponse

        // Convert the Credential Manager response to a standard WebAuthn response
        return Json.decodeFromString(androidPasskeyResponse.registrationResponseJson)
    }

    suspend fun deletePasskeyFromAuthenticators(
        id: String,
        rpId: String = Constants.RECIPE_WEB_HOST
    ) {
        try {
            credentialManager.signalCredentialState(
                SignalUnknownCredentialRequest(
                    requestJson = JSONObject().apply {
                        put("rpId", rpId)
                        put("credentialId", id)
                    }.toString()
                )
            )
            Log.d(TAG, "Signaled all authenticators to delete passkey $id with RP ID $rpId")
        } catch (ex: SignalCredentialStateException) {
            Log.w(TAG, "Failed to delete the passkey from all authenticators " +
                    "(ID: $id, RP ID: $rpId). Please delete them manually. :: " +
                    "error: ${ex.localizedMessage}")
        }
    }

    suspend fun syncPasskeysWithServer(
        ids: List<String>,
        rpId: String = Constants.RECIPE_WEB_HOST,
        userId: String
    ) {
        try {
            credentialManager.signalCredentialState(
                SignalAllAcceptedCredentialIdsRequest(
                    requestJson = JSONObject().apply {
                        put("rpId", rpId)
                        put("userId", userId.base64UrlEncode())
                        // The passkey IDs are already base64 URL-encoded
                        put("allAcceptedCredentialIds", JSONArray(ids))
                    }.toString()
                )
            )
            Log.d(TAG, "Signaled all authenticators to sync ${ids.size} ${
                if (ids.size == 1) "passkey" else "passkeys"
            } for user $userId and RP ID $rpId: [${ids.joinToString(", ")}]")
        } catch (ex: SignalCredentialStateException) {
            Log.w(TAG, "Failed to sync all passkeys with all authenticators " +
                    "(User ID: $userId, RP ID: $rpId). Please delete the rest manually. :: " +
                    "error: ${ex.localizedMessage}")
        }
    }

    suspend fun updateUsername(
        username: String,
        rpId: String = Constants.RECIPE_WEB_HOST,
        userId: String
    ) {
        try {
            credentialManager.signalCredentialState(
                SignalCurrentUserDetailsRequest(
                    requestJson = JSONObject().apply {
                        put("rpId", rpId)
                        put("userId", userId.base64UrlEncode())
                        put("name", username)
                        put("displayName", "")
                    }.toString()
                )
            )
            Log.d(TAG, "Signaled all authenticators to set the username for user ID " +
                    "$userId and RP ID $rpId to $username")
        } catch (ex: SignalCredentialStateException) {
            Log.w(TAG, "Failed to update the username to $username for all authenticators " +
                    "(User ID: $userId, RP ID: $rpId). Please update manually. :: " +
                    "error: ${ex.localizedMessage}")
        }
    }
}
