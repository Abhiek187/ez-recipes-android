package com.abhiek.ezrecipes.ui.profile

import android.net.Uri
import android.util.Log
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.data.models.Provider
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.Encryptor
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.security.KeyStore
import javax.crypto.Cipher

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class ProfileViewModelTest {
    private lateinit var mockChefService: MockChefService
    private lateinit var mockRecipeService: MockRecipeService
    private lateinit var mockDataStoreService: DataStoreService
    private lateinit var mockPasskeyManager: PasskeyManager
    private lateinit var viewModel: ProfileViewModel

    private val mockToken = Constants.Mocks.CHEF.token
    private val mockEncryptedToken = mockToken.toByteArray()

    @MockK
    private lateinit var keyStore: KeyStore
    @MockK
    private lateinit var uri: Uri

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
    }

    private fun mockEncryptor() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns keyStore
        justRun { keyStore.load(any()) }

        mockkStatic(Cipher::class)
        every { Cipher.getInstance(any()) } returns mockk()

        mockkObject(Encryptor)
        every { Encryptor.encrypt(any()) } returns mockEncryptedToken
        every { Encryptor.decrypt(any()) } returns mockToken
    }

    @BeforeEach
    fun setUp() {
        mockChefService = MockChefService
        mockRecipeService = MockRecipeService
        mockDataStoreService = mockkClass(DataStoreService::class) {
            coJustRun { incrementRecipesViewed() }
            coEvery { getToken() } returns mockEncryptedToken
            coJustRun { saveToken(any()) }
            coJustRun { deleteToken() }
        }
        mockPasskeyManager = mockkClass(PasskeyManager::class) {
            coEvery { getPasskey(any()) } returns mockk()
            coEvery { createPasskey(any()) } returns mockk()
        }
        viewModel = ProfileViewModel(
            chefRepository = ChefRepository(mockChefService),
            recipeRepository = RecipeRepository(mockRecipeService),
            dataStoreService = mockDataStoreService,
            passkeyManager = mockPasskeyManager
        )

        mockLog()
        mockEncryptor()
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns uri
    }

    @AfterEach
    fun tearDown() {
        // Revert back to the defaults
        mockRecipeService.isSuccess = true
        mockChefService.isSuccess = true
        mockChefService.isEmailVerified = true
    }

    @Test
    fun createAccountSuccess() = runTest {
        // Given the user's credentials
        val username = "test@example.com"
        val password = "test1234"

        // When creating an account
        viewModel.createAccount(username, password)

        // Then a new chef should be created
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = username,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = listOf(),
            passkeys = listOf(),
            ratings = mapOf(),
            recentRecipes = mapOf(),
            favoriteRecipes = listOf(),
            token = mockChefService.loginResponse.token
        ))

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun createAccountError() = runTest {
        // Given the user's credentials
        val username = "test@example.com"
        val password = "test1234"

        // When creating an account and an error occurs
        mockChefService.isSuccess = false
        viewModel.createAccount(username, password)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
    }

    @Test
    fun sendVerificationEmailSuccess() = runTest {
        // Given a valid token
        // When sending a verification email
        viewModel.sendVerificationEmail()

        // Then the email should be sent
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun sendVerificationEmailError() = runTest {
        // Given valid token
        // When sending a verification email and an error occurs
        mockChefService.isSuccess = false
        viewModel.sendVerificationEmail()

        // Then an error is shown
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun sendVerificationEmailNoToken() = runTest {
        // Given no token
        coEvery { mockDataStoreService.getToken() } returns null

        // When sending a verification email
        viewModel.sendVerificationEmail()

        // Then an error alert isn't shown
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun resetPasswordSuccess() = runTest {
        // Given an email
        val email = "test@example.com"

        // When resetting the password
        viewModel.resetPassword(email)

        // Then an email should be sent
        assertTrue(viewModel.emailSent)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
    }

    @Test
    fun resetPasswordError() = runTest {
        // Given an email
        val email = "test@example.com"

        // When resetting the password and an error occurs
        mockChefService.isSuccess = false
        viewModel.resetPassword(email)

        // Then an error is shown
        assertFalse(viewModel.emailSent)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
    }

    @Test
    fun getChefSuccess() = runTest {
        // Given a valid token
        // When getting the chef's profile
        viewModel.getChef()

        // Then the chef is saved and the user is authenticated
        assertEquals(viewModel.chef, mockChefService.chef)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.authState, AuthState.AUTHENTICATED)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun getChefError() = runTest {
        // Given a valid token
        // When getting the chef's profile and an error occurs
        mockChefService.isSuccess = false
        viewModel.getChef()

        // Then the user is unauthenticated
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun getChefNoToken() = runTest {
        // Given no token
        coEvery { mockDataStoreService.getToken() } returns null

        // When getting the chef's profile
        viewModel.getChef()

        // Then the user is unauthenticated
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)

        coVerify { mockDataStoreService.getToken() }
        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun loginSuccess() = runTest {
        // Given the user's credentials
        val username = "test@example.com"
        val password = "test1234"

        // When logging in
        viewModel.login(username, password)

        // Then the user should be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertEquals(viewModel.authState, AuthState.AUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun loginEmailNotVerified() = runTest {
        // Given the user hasn't verified their email
        val username = "test@example.com"
        val password = "test1234"

        // When logging in
        mockChefService.isEmailVerified = false
        viewModel.login(username, password)

        // Then a new chef should be created, but the user shouldn't be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = false,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertNotEquals(viewModel.authState, AuthState.AUTHENTICATED)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun loginError() = runTest {
        // Given the user's credentials
        val username = "test@example.com"
        val password = "test1234"

        // When logging in and an error occurs
        mockChefService.isSuccess = false
        viewModel.login(username, password)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
    }

    @Test
    fun logoutSuccess() = runTest {
        // Given a valid token
        // When logging out
        viewModel.logout()

        // Then the user is unauthenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertNull(viewModel.chef)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun logoutError() = runTest {
        // Given a valid token
        // When logging out and an error occurs
        mockChefService.isSuccess = false
        viewModel.logout()

        // Then an error is shown
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun logoutNoToken() = runTest {
        // Given no token
        coEvery { mockDataStoreService.getToken() } returns null

        // When logging out
        viewModel.logout()

        // Then the user is unauthenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertNull(viewModel.chef)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)

        coVerify { mockDataStoreService.getToken() }
        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun getAuthUrlsSuccess() = runTest {
        // Given no network errors
        // When getting all the auth URLs
        viewModel.getAuthUrls()

        // Then the URLs should be saved as a dictionary
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.authUrls.size, Constants.Mocks.AUTH_URLS.size)
        for (provider in Provider.entries) {
            assertNotNull(viewModel.authUrls[provider])
        }
    }

    @Test
    fun getAuthUrlsError() = runTest {
        // Given a network error
        mockChefService.isSuccess = false
        // When getting all the auth URLs
        viewModel.getAuthUrls()

        // Then the auth URLs should be empty
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
        assertTrue(viewModel.authUrls.isEmpty())
    }

    @Test
    fun loginWithOAuthSuccess() = runTest {
        // Given a code, provider, and token
        val code = "abc123"
        val provider = Provider.GOOGLE

        // When linking the provider
        viewModel.loginWithOAuth(code, provider)

        // Then the chef should be updated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertEquals(viewModel.authState, AuthState.AUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)
        assertTrue(viewModel.accountLinked)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun loginWithOAuthError() = runTest {
        // Given a code, provider, and token
        val code = "abc123"
        val provider = Provider.GOOGLE

        // When linking the provider and an error occurs
        mockChefService.isSuccess = false
        viewModel.loginWithOAuth(code, provider)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun loginWithOAuthNoToken() = runTest {
        // Given a code, provider, and no token
        val code = "abc123"
        val provider = Provider.GOOGLE
        coEvery { mockDataStoreService.getToken() } returns null

        // When logging in with the provider
        viewModel.loginWithOAuth(code, provider)

        // Then the user should be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertEquals(viewModel.authState, AuthState.AUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)
        assertFalse(viewModel.accountLinked)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun loginWithOAuthEmailNotVerified() = runTest {
        // Given a code, provider, and no token
        val code = "abc123"
        val provider = Provider.GOOGLE
        coEvery { mockDataStoreService.getToken() } returns null

        // When logging in with the provider, and the email isn't verified
        mockChefService.isEmailVerified = false
        viewModel.loginWithOAuth(code, provider)

        // Then a new chef should be created, but the user shouldn't be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = false,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertNotEquals(viewModel.authState, AuthState.AUTHENTICATED)
        assertFalse(viewModel.accountLinked)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun unlinkOAuthProviderSuccess() = runTest {
        // Given a provider
        val provider = Provider.FACEBOOK

        // When unlinking the provider
        viewModel.unlinkOAuthProvider(provider)

        // Then the provider should be removed from the chef
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertTrue(viewModel.accountUnlinked)

        verify { Encryptor.encrypt(mockChefService.mockToken.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun unlinkOAuthProviderError() = runTest {
        // Given a provider
        val provider = Provider.FACEBOOK

        // When unlinking the provider and an error occurs
        mockChefService.isSuccess = false
        viewModel.unlinkOAuthProvider(provider)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
        assertFalse(viewModel.accountUnlinked)
    }

    @Test
    fun unlinkOAuthProviderNoToken() = runTest {
        // Given a provider and no token
        val provider = Provider.FACEBOOK
        coEvery { mockDataStoreService.getToken() } returns null

        // When unlinking the provider
        viewModel.unlinkOAuthProvider(provider)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.accountUnlinked)
    }

    @Test
    fun loginWithPasskeySuccess() = runTest {
        // Given a successful passkey login

        // When logging in with a passkey
        viewModel.loginWithPasskey(mockChefService.chef.email)

        // Then the user should be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = mockChefService.chef.email,
            emailVerified = mockChefService.loginResponse.emailVerified,
            providerData = mockChefService.chef.providerData,
            passkeys = mockChefService.chef.passkeys,
            ratings = mockChefService.chef.ratings,
            recentRecipes = mockChefService.chef.recentRecipes,
            favoriteRecipes = mockChefService.chef.favoriteRecipes,
            token = mockChefService.loginResponse.token
        ))
        assertEquals(viewModel.authState, AuthState.AUTHENTICATED)
        assertFalse(viewModel.openLoginDialog)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun loginWithPasskeyError() = runTest {
        // Given an error during passkey login

        // When logging in with a passkey
        mockChefService.isSuccess = false
        viewModel.loginWithPasskey(mockChefService.chef.email)

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, RecipeError("Error"))
        assertTrue(viewModel.showAlert)
    }

    @Test
    fun loginWithPasskeyCanceled() = runTest {
        // Given the user cancels passkey login

        // When logging in with a passkey
        viewModel.loginWithPasskey(mockChefService.chef.email)

        // Then the error shouldn't be shown
        assertNull(viewModel.chef)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
    }

    @Test
    fun createNewPasskeySuccess() = runTest {
        // Given a successful passkey creation

        // When creating a new passkey
        viewModel.createNewPasskey()

        // Then the chef should have a new passkey
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef?.passkeys?.size, 1)

        verify { Encryptor.encrypt(mockChefService.loginResponse.token) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun createNewPasskeyError() = runTest {
        // Given an error during passkey creation

        // When creating a new passkey
        mockChefService.isSuccess = false
        viewModel.createNewPasskey()

        // Then an error is shown
        assertNull(viewModel.chef)
        assertEquals(viewModel.recipeError, RecipeError("Error"))
        assertTrue(viewModel.showAlert)
    }

    @Test
    fun createNewPasskeyCanceled() = runTest {
        // Given the user cancels passkey creation

        // When creating a new passkey
        viewModel.createNewPasskey()

        // Then the error shouldn't be shown
        assertNull(viewModel.chef)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
    }

    @Test
    fun deletePasskeySuccess() = runTest {
        // Given a passkey to delete
        val credentialId = "test-credential-id"

        // When deleting the passkey
        viewModel.deletePasskey(credentialId)

        // Then the passkey should be removed from the chef
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, mockChefService.chef)
        assertTrue(viewModel.passkeyDeleted)

        verify { Encryptor.encrypt(mockChefService.mockToken.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun deletePasskeyError() = runTest {
        // Given a passkey to delete
        val credentialId = "test-credential-id"

        // When deleting the passkey and an error occurs
        mockChefService.isSuccess = false
        viewModel.deletePasskey(credentialId)

        // Then an error is shown
        assertEquals(viewModel.recipeError, mockChefService.tokenError)
        assertFalse(viewModel.passkeyDeleted)
    }

    @Test
    fun deletePasskeyNoToken() = runTest {
        // Given a passkey to delete and no token
        val credentialId = "test-credential-id"
        coEvery { mockDataStoreService.getToken() } returns null

        // When deleting the passkey
        viewModel.deletePasskey(credentialId)

        // Then an error is shown
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.passkeyDeleted)
    }

    @Test
    fun updateEmailSuccess() = runTest {
        // Given a valid token
        val newEmail = "mock@example.com"

        // When updating the chef's email
        viewModel.updateEmail(newEmail)

        // Then an email should be sent
        assertTrue(viewModel.emailSent)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun updateEmailError() = runTest {
        // Given a valid token
        val newEmail = "mock@example.com"

        // When updating the chef's email and an error occurs
        mockChefService.isSuccess = false
        viewModel.updateEmail(newEmail)

        // Then an error is shown
        assertFalse(viewModel.emailSent)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun updateEmailNoToken() = runTest {
        // Given no token
        val newEmail = "mock@example.com"
        coEvery { mockDataStoreService.getToken() } returns null

        // When updating the chef's email
        viewModel.updateEmail(newEmail)

        // Then an error alert isn't shown
        assertFalse(viewModel.emailSent)
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun updatePasswordSuccess() = runTest {
        // Given a valid token
        val newPassword = "mockPassword"

        // When updating the chef's password
        viewModel.updatePassword(newPassword)

        // Then the password should be updated and the user should be signed out
        assertTrue(viewModel.passwordUpdated)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun updatePasswordError() = runTest {
        // Given a valid token
        val newPassword = "mockPassword"

        // When updating the chef's password and an error occurs
        mockChefService.isSuccess = false
        viewModel.updatePassword(newPassword)

        // Then an error is shown
        assertFalse(viewModel.passwordUpdated)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun updatePasswordNoToken() = runTest {
        // Given no token
        val newPassword = "mockPassword"
        coEvery { mockDataStoreService.getToken() } returns null

        // When updating the chef's password
        viewModel.updatePassword(newPassword)

        // Then an error alert isn't shown
        assertFalse(viewModel.passwordUpdated)
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun deleteAccountSuccess() = runTest {
        // Given a valid token
        // When deleting the chef's account
        viewModel.deleteAccount()

        // Then the chef should be deleted and unauthenticated
        assertNull(viewModel.chef)
        assertEquals(viewModel.authState, AuthState.UNAUTHENTICATED)
        assertTrue(viewModel.accountDeleted)
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        coVerify { mockDataStoreService.deleteToken() }
    }

    @Test
    fun deleteAccountError() = runTest {
        // Given a valid token
        // When deleting the chef's account and an error occurs
        mockChefService.isSuccess = false
        viewModel.deleteAccount()

        // Then an error is shown
        assertFalse(viewModel.accountDeleted)
        assertEquals(viewModel.recipeError, mockChefService.tokenError)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun deleteAccountNoToken() = runTest {
        // Given no token
        coEvery { mockDataStoreService.getToken() } returns null

        // When deleting the chef's account
        viewModel.deleteAccount()

        // Then an error alert isn't shown
        assertFalse(viewModel.accountDeleted)
        assertEquals(viewModel.recipeError, RecipeError(Constants.NO_TOKEN_FOUND))
        assertFalse(viewModel.showAlert)

        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun getAllFavoriteRecipesSuccess() = runTest {
        // Given a chef with favorite recipes
        viewModel.getChef()

        // When getting all favorite recipes
        viewModel.getAllFavoriteRecipes()

        // Then all the recipes are fetched
        assertEquals(viewModel.favoriteRecipes.value.size,
            mockChefService.chef.favoriteRecipes.size)
    }

    @Test
    fun getAllFavoriteRecipesError() = runTest {
        // Given a chef with favorite recipes
        viewModel.getChef()

        // When getting all favorite recipes and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.getAllFavoriteRecipes()

        // Then no recipes are fetched
        assertEquals(viewModel.favoriteRecipes.value.size, 0)
    }

    @Test
    fun getAllRecentRecipesSuccess() = runTest {
        // Given a chef with recent recipes
        viewModel.getChef()

        // When getting all recent recipes
        viewModel.getAllRecentRecipes()

        // Then all the recipes are fetched
        assertEquals(viewModel.recentRecipes.value.size,
            mockChefService.chef.recentRecipes.size)
    }

    @Test
    fun getAllRecentRecipesError() = runTest {
        // Given a chef with recent recipes
        viewModel.getChef()

        // When getting all recent recipes and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.getAllRecentRecipes()

        // Then no recipes are fetched
        assertEquals(viewModel.recentRecipes.value.size, 0)
    }

    @Test
    fun getAllRatedRecipesSuccess() = runTest {
        // Given a chef with rated recipes
        viewModel.getChef()

        // When getting all rated recipes
        viewModel.getAllRatedRecipes()

        // Then all the recipes are fetched
        assertEquals(viewModel.ratedRecipes.value.size,
            mockChefService.chef.ratings.size)
    }

    @Test
    fun getAllRatedRecipesError() = runTest {
        // Given a chef with rated recipes
        viewModel.getChef()

        // When getting all rated recipes and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.getAllRatedRecipes()

        // Then no recipes are fetched
        assertEquals(viewModel.ratedRecipes.value.size, 0)
    }

    @Test
    fun updateRecipeViewsSuccess() = runTest {
        // Given a recipe and a valid token
        val recipe = mockRecipeService.recipes[0]
        viewModel.getChef()

        // When updating the recipe views
        viewModel.updateRecipeViews(recipe)

        // Then the recipe views should be updated
        assertEquals(viewModel.chef?.recentRecipes?.contains(recipe.id.toString()), true)

        coVerify { mockDataStoreService.incrementRecipesViewed() }
        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun updateRecipeViewsError() = runTest {
        // Given a recipe and a valid token
        val recipe = mockRecipeService.recipes[0]
        viewModel.getChef()

        // When updating the recipe views and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.updateRecipeViews(recipe)

        // Then an error is logged
        coVerify { mockDataStoreService.incrementRecipesViewed() }
        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun updateRecipeViewsNoToken() = runTest {
        // Given a recipe and no token
        val recipe = mockRecipeService.recipes[0]
        coEvery { mockDataStoreService.getToken() } returns null

        // When updating the recipe views
        viewModel.updateRecipeViews(recipe)

        // Then an error is logged
        coVerify { mockDataStoreService.incrementRecipesViewed() }
        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun favoriteRecipeSuccess() = runTest {
        // Given a recipe and a valid token
        val recipeId = 1
        viewModel.getChef()

        // When adding the recipe to favorites
        viewModel.toggleFavoriteRecipe(recipeId, true)

        // Then the recipe should appear in the chef's favorites
        assertTrue(viewModel.chef?.favoriteRecipes?.contains(recipeId.toString()) == true)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun unFavoriteRecipeSuccess() = runTest {
        // Given a recipe and a valid token
        val recipeId = 1
        viewModel.getChef()

        // When removing the recipe from favorites
        viewModel.toggleFavoriteRecipe(recipeId, false)

        // Then the recipe shouldn't appear in the chef's favorites
        assertFalse(viewModel.chef?.favoriteRecipes?.contains(recipeId.toString()) != false)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun toggleFavoriteRecipeError() = runTest {
        // Given a recipe and a valid token
        val recipeId = 1
        viewModel.getChef()

        // When toggling a recipe as a favorite and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.toggleFavoriteRecipe(recipeId, true)

        // Then an error is logged
        assertFalse(viewModel.chef?.favoriteRecipes?.contains(recipeId.toString()) != false)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun toggleFavoriteRecipeNoToken() = runTest {
        // Given no token
        val recipeId = 1
        coEvery { mockDataStoreService.getToken() } returns null

        // When toggling a recipe as a favorite
        viewModel.toggleFavoriteRecipe(recipeId, true)

        // Then an error is logged
        coVerify { mockDataStoreService.getToken() }
    }

    @Test
    fun rateRecipeSuccess() = runTest {
        // Given a recipe and a valid token
        val recipeId = 1
        val rating = 4
        viewModel.getChef()

        // When rating the recipe
        viewModel.rateRecipe(recipeId, rating)

        // Then the rating should be saved with the chef
        assertEquals(viewModel.chef?.ratings?.get(recipeId.toString()), rating)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }

        assertNotNull(mockChefService.chefEmailResponse.token)
        verify { Encryptor.encrypt(mockChefService.chefEmailResponse.token!!) }
        coVerify { mockDataStoreService.saveToken(mockEncryptedToken) }
    }

    @Test
    fun rateRecipeError() = runTest {
        // Given a recipe and a valid token
        val recipeId = 1
        val rating = 4
        viewModel.getChef()

        // When rating the recipe and an error occurs
        mockRecipeService.isSuccess = false
        viewModel.rateRecipe(recipeId, rating)

        // Then an error is logged
        assertFalse(viewModel.chef?.ratings?.contains(recipeId.toString()) != false)

        coVerify { mockDataStoreService.getToken() }
        verify { Encryptor.decrypt(mockEncryptedToken) }
    }

    @Test
    fun rateRecipeNoToken() = runTest {
        // Given no token
        val recipeId = 1
        val rating = 4
        coEvery { mockDataStoreService.getToken() } returns null

        // When rating the recipe
        viewModel.rateRecipe(recipeId, rating)

        // Then an error is logged
        coVerify { mockDataStoreService.getToken() }
    }
}
