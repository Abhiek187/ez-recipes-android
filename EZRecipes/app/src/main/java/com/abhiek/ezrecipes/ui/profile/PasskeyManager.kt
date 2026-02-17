package com.abhiek.ezrecipes.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import com.abhiek.ezrecipes.data.models.ExistingPasskeyClientResponse
import com.abhiek.ezrecipes.data.models.NewPasskeyClientResponse
import com.abhiek.ezrecipes.data.models.PasskeyCreationOptions
import com.abhiek.ezrecipes.data.models.PasskeyRequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Helper class to manage passkey creation and retrieval using the Credential Manager
 *
 * Minimum Google Play Services version required: 23.08.15 (230815045)
 *
 * Check by running: `adb shell pm dump com.google.android.gms | grep version`
 */
class PasskeyManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val gson = Gson()

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getPasskey(
        serverPasskeyOptions: PasskeyRequestOptions
    ): ExistingPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val androidPasskeyOptions = GetPublicKeyCredentialOption(
            gson.toJson(serverPasskeyOptions)
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
        return gson.fromJson(
            androidPasskeyResponse.authenticationResponseJson,
            object: TypeToken<ExistingPasskeyClientResponse>() {}
        )
    }

    @SuppressLint("PublicKeyCredential")
    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun createPasskey(
        serverPasskeyOptions: PasskeyCreationOptions
    ): NewPasskeyClientResponse {
        // Convert the standard WebAuthn options to a Credential Manager request
        val androidPasskeyRequest = CreatePublicKeyCredentialRequest(
            gson.toJson(serverPasskeyOptions)
        )
        // Triggers the device to prompt for a passkey
        val androidPasskeyResponse = credentialManager.createCredential(
            context,
            androidPasskeyRequest
        ) as CreatePublicKeyCredentialResponse

        // Convert the Credential Manager response to a standard WebAuthn response
        return gson.fromJson(
            androidPasskeyResponse.registrationResponseJson,
            object: TypeToken<NewPasskeyClientResponse>() {}
        )
    }
}
