package com.abhiek.ezrecipes.ui.profile

import android.util.Log
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.Chef
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
    private lateinit var viewModel: ProfileViewModel

    private val mockToken = Constants.Mocks.CHEF.token
    private val mockEncryptedToken = mockToken.toByteArray()

    @MockK
    private lateinit var keyStore: KeyStore

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.d(any(), any<String>()) } returns 0
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
            coEvery { getToken() } returns mockEncryptedToken
            coJustRun { saveToken(any()) }
            coJustRun { deleteToken() }
        }
        viewModel = ProfileViewModel(
            chefRepository = ChefRepository(mockChefService),
            recipeRepository = RecipeRepository(mockRecipeService),
            dataStoreService = mockDataStoreService
        )

        mockLog()
        mockEncryptor()
    }

    @AfterEach
    fun tearDown() {
        // Revert back to the defaults
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
            email = username,
            emailVerified = mockChefService.loginResponse.emailVerified,
            ratings = mapOf(),
            recentRecipes = mapOf(),
            favoriteRecipes = listOf(),
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
        viewModel.createAccount(username, password)

        // Then a new chef should be created, but the user shouldn't be authenticated
        assertNull(viewModel.recipeError)
        assertFalse(viewModel.showAlert)
        assertEquals(viewModel.chef, Chef(
            uid = mockChefService.loginResponse.uid,
            email = username,
            emailVerified = mockChefService.loginResponse.emailVerified,
            ratings = mapOf(),
            recentRecipes = mapOf(),
            favoriteRecipes = listOf(),
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
        viewModel.createAccount(username, password)

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
}
