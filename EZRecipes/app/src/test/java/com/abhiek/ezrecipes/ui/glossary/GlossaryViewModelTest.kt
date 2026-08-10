package com.abhiek.ezrecipes.ui.glossary

import com.abhiek.ezrecipes.data.terms.MockTermsService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.ui.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
@ExtendWith(MockKExtension::class)
internal class GlossaryViewModelTest {
    private lateinit var mockTermsRepository: TermsRepository
    private lateinit var viewModel: GlossaryViewModel

    @BeforeEach
    fun setUp() {
        mockTermsRepository = mockkClass(TermsRepository::class)
        viewModel = GlossaryViewModel(mockTermsRepository)
    }

    @Test
    fun checkCachedTerms() = runTest {
        // Given a TermRepository with terms
        coEvery { mockTermsRepository.getTerms() } returns MockTermsService.terms

        // When checkCachedTerms() is called
        viewModel.checkCachedTerms()

        // Then the terms property should match the mock terms
        assertEquals(viewModel.terms, MockTermsService.terms)
    }
}
