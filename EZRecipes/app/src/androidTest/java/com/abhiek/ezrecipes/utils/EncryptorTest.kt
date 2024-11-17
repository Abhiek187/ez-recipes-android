package com.abhiek.ezrecipes.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class EncryptorTest {
    // AndroidKeyStore is only available on Android devices
    @Test
    fun encryptAndDecrypt() {
        // Given a string
        val strings = listOf("", "test", "The quick brown fox jumps over the lazy dog.")

        for (originalStr in strings) {
            // When it's encrypted and decrypted
            val encryptedStr = Encryptor.encrypt(originalStr)
            val decryptedStr = Encryptor.decrypt(encryptedStr)

            // Then the decrypted string should match the original string
            assertFalse(String(encryptedStr) == originalStr)
            assertEquals(originalStr, decryptedStr)
        }
    }

    @Test
    fun decryptedInvalidData() {
        // Given a byte array that doesn't represent encrypted data
        val invalidData = ByteArray(16) { it.toByte() }
        // When it's decrypted
        // Then an exception should be thrown
        assertThrows(Exception::class.java) {
            Encryptor.decrypt(invalidData)
        }
    }
}
