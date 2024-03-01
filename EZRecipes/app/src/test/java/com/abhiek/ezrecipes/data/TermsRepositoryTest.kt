package com.abhiek.ezrecipes.data

import com.abhiek.ezrecipes.data.terms.MockTermsService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.data.terms.TermsResult
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TermsRepositoryTest {
    private lateinit var mockService: MockTermsService
    private lateinit var termsRepository: TermsRepository

    @BeforeEach
    fun setUp() {
        mockService = MockTermsService
        termsRepository = TermsRepository(mockService)
    }

    @Test
    fun getTermsSuccess() = runTest {
        // Given an instance of TermsRepository
        // When getTerms() is called
        mockService.isSuccess = true
        val response = termsRepository.getTerms()

        // Then it should return a successful response
        assertTrue(response is TermsResult.Success)
        assertEquals((response as TermsResult.Success).response, Constants.Mocks.TERMS)
    }

    @Test
    fun getTermsError() = runTest {
        // Given an instance of TermsRepository
        // When getTerms() is called with isSuccess = false
        mockService.isSuccess = false
        val response = termsRepository.getTerms()

        // Then it should return an error
        assertTrue(response is TermsResult.Error)
        assertEquals((response as TermsResult.Error).recipeError, mockService.recipeError)
    }
}
