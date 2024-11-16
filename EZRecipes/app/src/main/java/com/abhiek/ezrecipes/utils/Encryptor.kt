package com.abhiek.ezrecipes.utils

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec

/**
 * Helper object for interfacing with the Android KeyStore
 */
object Encryptor {
    private const val KEYSTORE_ALIAS = "SecretKeyAlias"
    // Provider specific to Android devices, gotten from Security.getProviders()
    // https://stackoverflow.com/questions/7560974/what-crypto-algorithms-does-android-support
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256 // AES 256-bit encryption
    private const val IV_SIZE = 128 // Initialization Vector (IV) size in bits
    private const val USER_AUTH_TIMEOUT_SECONDS = 30

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }
    private val cipher = Cipher.getInstance(ALGORITHM)

    private fun getSecretKey(): SecretKey {
        return if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            (keyStore.getEntry(KEYSTORE_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(KEY_SIZE)
                // Requires a secure lock screen and biometrics
                setUserAuthenticationRequired(false)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setUnlockedDeviceRequired(true)
                    // Slower, but more secure than TEE (Trusted Execution Environment)
                    // Certain devices running Android P or later may have a StrongBox
                    // The Android emulator's security is purely software-based
                    setIsStrongBoxBacked(false)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setUserAuthenticationParameters(
                        USER_AUTH_TIMEOUT_SECONDS,
                        KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
                    )
                } else {
                    @Suppress("DEPRECATION")
                    setUserAuthenticationValidityDurationSeconds(USER_AUTH_TIMEOUT_SECONDS)
                }
            }.build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getKeyInfo(secretKey: SecretKey): KeyInfo {
        // For debugging purposes
        val secretKeyFactory = SecretKeyFactory.getInstance(secretKey.algorithm, ANDROID_KEYSTORE)
        return secretKeyFactory.getKeySpec(secretKey, KeyInfo::class.java) as KeyInfo
    }

    /**
     * Encrypts the provided string
     * @param data The string to encrypt
     * @return The encrypted data in bytes
     */
    fun encrypt(data: String): ByteArray {
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return encryptedData
    }

    /**
     * Decrypts the provided encrypted data
     * @param encryptedData The data to decrypt in bytes
     * @return The decrypted string
     */
    fun decrypt(encryptedData: ByteArray): String {
        val secretKey = getSecretKey()
        val spec = GCMParameterSpec(IV_SIZE, cipher.iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }
}
