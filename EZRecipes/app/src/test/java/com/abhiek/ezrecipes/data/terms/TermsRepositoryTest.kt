package com.abhiek.ezrecipes.data.terms

import android.util.Log
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.utils.Constants
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class TermsRepositoryTest {
    private lateinit var mockTermsService: MockTermsService
    private lateinit var mockDataStoreService: DataStoreService
    private lateinit var termsRepository: TermsRepository

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
    }

    @BeforeEach
    fun setUp() {
        mockTermsService = MockTermsService
        mockDataStoreService = mockkClass(DataStoreService::class) {
            coJustRun { saveTerms(any()) }
        }
        termsRepository = TermsRepository(mockTermsService, mockDataStoreService)

        mockLog()
    }

    @Test
    fun fetchTermsSuccess() = runTest {
        // Given an instance of TermsRepository
        // When getTerms() is called
        mockTermsService.isSuccess = true
        val response = termsRepository.fetchTerms()

        // Then it should return a successful response
        assertTrue(response is TermsResult.Success)
        assertEquals(
            (response as TermsResult.Success).response,
            Constants.Mocks.TERMS
        )
    }

    @Test
    fun fetchTermsError() = runTest {
        // Given an instance of TermsRepository
        // When getTerms() is called with isSuccess = false
        mockTermsService.isSuccess = false
        val response = termsRepository.fetchTerms()

        // Then it should return an error
        assertTrue(response is TermsResult.Error)
        assertEquals(
            (response as TermsResult.Error).recipeError,
            mockTermsService.recipeError
        )
    }

    @Test
    fun getCachedTerms() = runTest {
        // Given a DataStore with terms
        coEvery { mockDataStoreService.getTerms() } returns mockTermsService.terms

        // When getTerms() is called
        val terms = termsRepository.getTerms()

        // Then the terms should match the mock terms
        assertEquals(terms, mockTermsService.terms)
    }

    @Test
    fun getTermsFetchSuccess() = runTest {
        // Given a DataStore with no terms
        coEvery { mockDataStoreService.getTerms() } returns null

        // When getTerms() is called with a successful API call
        mockTermsService.isSuccess = true
        val terms = termsRepository.getTerms()

        // Then the terms should be saved and match the mock terms
        assertEquals(terms, mockTermsService.terms)
        coVerify { mockDataStoreService.saveTerms(mockTermsService.terms) }
    }

    @Test
    fun getTermsFetchError() = runTest {
        // Given a DataStore with no terms
        coEvery { mockDataStoreService.getTerms() } returns null

        // When getTerms() is called with an unsuccessful API call
        mockTermsService.isSuccess = false
        val terms = termsRepository.getTerms()

        // Then the terms should be empty
        assertEquals(terms, listOf<Term>())
    }
}
