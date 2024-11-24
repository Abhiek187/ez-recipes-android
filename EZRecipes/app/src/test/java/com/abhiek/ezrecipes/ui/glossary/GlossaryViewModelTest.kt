package com.abhiek.ezrecipes.ui.glossary

import android.util.Log
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.MockTermsService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class GlossaryViewModelTest {
    private lateinit var mockTermsService: MockTermsService
    private lateinit var mockDataStoreService: DataStoreService
    private lateinit var viewModel: GlossaryViewModel

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
        viewModel = GlossaryViewModel(
            termsRepository = TermsRepository(mockTermsService),
            dataStoreService = mockDataStoreService
        )

        mockLog()
    }

    @Test
    fun checkCachedTermsExist() = runTest {
        // Given a DataStore with terms
        coEvery { mockDataStoreService.getTerms() } returns mockTermsService.terms

        // When checkCachedTerms() is called
        viewModel.checkCachedTerms()

        // Then the terms property should match the mock terms
        assertEquals(viewModel.terms, mockTermsService.terms)
    }

    @Test
    fun checkCachedTermsFetchSuccess() = runTest {
        // Given a DataStore with no terms
        coEvery { mockDataStoreService.getTerms() } returns null

        // When checkCachedTerms() is called with a successful API call
        mockTermsService.isSuccess = true
        viewModel.checkCachedTerms()

        // Then the terms property should be saved and match the mock terms
        assertEquals(viewModel.terms, mockTermsService.terms)
        coVerify { mockDataStoreService.saveTerms(mockTermsService.terms) }
    }

    @Test
    fun checkCachedTermsFetchError() = runTest {
        // Given a DataStore with no terms
        coEvery { mockDataStoreService.getTerms() } returns null

        // When checkCachedTerms() is called with an unsuccessful API call
        mockTermsService.isSuccess = false
        viewModel.checkCachedTerms()

        // Then the terms property should be empty
        assertEquals(viewModel.terms, listOf<Term>())
    }
}
