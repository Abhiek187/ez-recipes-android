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
    private const val KEY_SIZE_BITS = 256 // AES 256-bit encryption
    private const val IV_SIZE_BYTES = 12 // Initialization Vector (IV) size in bytes
    private const val AUTH_TAG_SIZE_BITS = 128 // Authentication tag size in bits
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
                setKeySize(KEY_SIZE_BITS)
                // Requires a secure lock screen and biometrics
                setUserAuthenticationRequired(false)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // This is bugged on Android 12-14
                    setUnlockedDeviceRequired(
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                    )
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
        // The IV is generated using SecureRandom (/dev/urandom)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data.toByteArray())
        // Add the IV to the encrypted data so it can be used for decryption
        return iv + encryptedData
    }

    /**
     * Decrypts the provided encrypted data
     * @param encryptedData The data to decrypt in bytes
     * @return The decrypted string
     */
    fun decrypt(encryptedData: ByteArray): String {
        val secretKey = getSecretKey()
        val iv = encryptedData.sliceArray(0 until IV_SIZE_BYTES)
        val encryptedText = encryptedData.sliceArray(
            IV_SIZE_BYTES until encryptedData.size
        )
        val spec = GCMParameterSpec(AUTH_TAG_SIZE_BITS, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedText)
        return String(decryptedData)
    }
}
