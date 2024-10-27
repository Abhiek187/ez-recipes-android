package com.abhiek.ezrecipes.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.models.Profile

class ProfileViewModel: ViewModel() {
    var authState by mutableStateOf(AuthState.UNAUTHENTICATED)
    var profile by mutableStateOf<Profile?>(null)
        private set

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
