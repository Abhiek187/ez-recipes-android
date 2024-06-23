package com.abhiek.ezrecipes.ui.glossary

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.data.terms.TermsService

class GlossaryViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlossaryViewModel::class.java)) {
            return GlossaryViewModel(
                termsRepository = TermsRepository(
                    termsService = TermsService.instance
                ),
                dataStoreService = DataStoreService(context)
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
