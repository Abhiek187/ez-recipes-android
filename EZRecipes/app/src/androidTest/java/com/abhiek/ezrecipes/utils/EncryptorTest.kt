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
        val strings = listOf("", "test", "The quick brown fox jumps over the lazy dog.")

        for (originalStr in strings) {
            // Given a string
            // When it's encrypted and decrypted
            val encryptedStr = Encryptor.encrypt(originalStr)
            val decryptedStr = Encryptor.decrypt(encryptedStr)

            // Then the decrypted string should match the original string
            println("originalStr: $originalStr")
            println("encryptedStr: ${String(encryptedStr)}")
            println("decryptedStr: $decryptedStr")
            assertFalse(String(encryptedStr) == originalStr)
            assertEquals(originalStr, decryptedStr)
        }
    }
}
