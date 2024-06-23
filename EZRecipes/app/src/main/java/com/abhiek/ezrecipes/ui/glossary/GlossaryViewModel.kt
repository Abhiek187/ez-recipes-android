package com.abhiek.ezrecipes.ui.glossary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.TermsRepository

class GlossaryViewModel(
    private val termsRepository: TermsRepository,
    private val dataStoreService: DataStoreService
): ViewModel() {
    var terms by mutableStateOf<List<Term>>(listOf())
}
