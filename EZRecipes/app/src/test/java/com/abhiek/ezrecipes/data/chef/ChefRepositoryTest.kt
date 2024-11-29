package com.abhiek.ezrecipes.data.chef

import com.abhiek.ezrecipes.data.models.ChefUpdate
import com.abhiek.ezrecipes.data.models.LoginCredentials
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ChefRepositoryTest {
    private lateinit var mockService: MockChefService
    private lateinit var chefRepository: ChefRepository

    private val mockToken = Constants.Mocks.CHEF.token
    private val mockLoginCredentials = LoginCredentials(
        email = "test@example.com",
        password = "test1234"
    )
    private val mockChefUpdate = ChefUpdate(
        type = "password",
        email = "test@example.com",
        password = "test1234"
    )

    @BeforeEach
    fun setUp() {
        mockService = MockChefService
        chefRepository = ChefRepository(mockService)
    }

    @Test
    fun getChefSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.getChef(mockToken)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, mockService.chef)
    }

    @Test
    fun getChefError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.getChef(mockToken)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun createChefSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.createChef(mockLoginCredentials)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, mockService.loginResponse)
    }

    @Test
    fun createChefError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.createChef(mockLoginCredentials)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun updateChefSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.updateChef(
            fields = mockChefUpdate,
            token = mockToken
        )

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, mockService.chefEmailResponse)
    }

    @Test
    fun updateChefError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.updateChef(
            fields = mockChefUpdate,
            token = mockToken
        )

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun deleteChefSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.deleteChef(mockToken)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, Unit)
    }

    @Test
    fun deleteChefError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.deleteChef(mockToken)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun verifyEmailSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.verifyEmail(mockToken)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, mockService.chefEmailResponse)
    }

    @Test
    fun verifyEmailError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.verifyEmail(mockToken)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun loginSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.login(mockLoginCredentials)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, mockService.loginResponse)
    }

    @Test
    fun loginError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.login(mockLoginCredentials)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }

    @Test
    fun logoutSuccess() = runTest {
        mockService.isSuccess = true
        val response = chefRepository.logout(mockToken)

        assertTrue(response is ChefResult.Success)
        assertEquals((response as ChefResult.Success).response, Unit)
    }

    @Test
    fun logoutError() = runTest {
        mockService.isSuccess = false
        val response = chefRepository.logout(mockToken)

        assertTrue(response is ChefResult.Error)
        assertEquals((response as ChefResult.Error).recipeError, mockService.tokenError)
    }
}
