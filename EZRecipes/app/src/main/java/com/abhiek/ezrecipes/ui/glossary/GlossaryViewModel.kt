package com.abhiek.ezrecipes.ui.glossary

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.data.terms.TermsResult
import kotlinx.coroutines.launch

class GlossaryViewModel(
    private val termsRepository: TermsRepository,
    private val dataStoreService: DataStoreService
): ViewModel() {
    var terms by mutableStateOf<List<Term>>(listOf())
        private set

    fun checkCachedTerms() {
        viewModelScope.launch {
            // Check if terms need to be cached
            if (dataStoreService.getTerms() != null) return@launch

            when (val result = termsRepository.getTerms()) {
                is TermsResult.Success -> {
                    terms = result.response
                    dataStoreService.saveTerms(terms)
                }
                is TermsResult.Error -> {
                    // No need to handle errors besides logging
                    terms = listOf()
                    Log.w(
                        "GlossaryViewModel",
                        "Failed to get terms :: error: ${result.recipeError}"
                    )
                }
            }
        }
    }
}
