package com.abhiek.ezrecipes.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.Chef

class ProfileViewModel(
    private val chefRepository: ChefRepository
): ViewModel() {
    var authState by mutableStateOf(AuthState.UNAUTHENTICATED)
    var chef by mutableStateOf<Chef?>(null)

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
