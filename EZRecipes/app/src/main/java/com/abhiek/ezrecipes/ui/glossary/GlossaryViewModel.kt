package com.abhiek.ezrecipes.ui.glossary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.terms.Term
import com.abhiek.ezrecipes.data.terms.TermsRepository
import kotlinx.coroutines.launch

class GlossaryViewModel(private val termsRepository: TermsRepository): ViewModel() {
    var terms by mutableStateOf<List<Term>>(listOf())
        private set

    fun checkCachedTerms() {
        viewModelScope.launch {
            terms = termsRepository.getTerms()
        }
    }
}
