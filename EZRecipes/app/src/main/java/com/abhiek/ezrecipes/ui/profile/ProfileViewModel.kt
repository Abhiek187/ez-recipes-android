package com.abhiek.ezrecipes.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.ChefResult
import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.Encryptor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val chefRepository: ChefRepository,
    private val recipeRepository: RecipeRepository,
    private val dataStoreService: DataStoreService
): ViewModel() {
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set

    var authState by mutableStateOf(AuthState.LOADING)
    var isLoading by mutableStateOf(false)
    var chef by mutableStateOf<Chef?>(null)
    var favoriteRecipes by mutableStateOf<List<Recipe>>(listOf())
    var recentRecipes by mutableStateOf<List<Recipe>>(listOf())
    var ratedRecipes by mutableStateOf<List<Recipe>>(listOf())
    var openLoginDialog by mutableStateOf(false)
    var showAlert by mutableStateOf(false)
    var emailSent by mutableStateOf(false)
    var passwordUpdated by mutableStateOf(false)
    var accountDeleted by mutableStateOf(false)

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private suspend fun saveToken(token: String) {
        // Encrypt the ID token and save it to the DataStore
        try {
            val encryptedToken = Encryptor.encrypt(token)
            dataStoreService.saveToken(encryptedToken)
            Log.d(TAG, "Saved ID token to the DataStore")
        } catch (error: Exception) {
            Log.e(TAG, "Error saving token: ${error.printStackTrace()}")
        }
    }

    private suspend fun getToken(): String? {
        // Get the ID token from the DataStore and decrypt it
        try {
            val encryptedToken = dataStoreService.getToken()

            return if (encryptedToken != null) {
                val token = Encryptor.decrypt(encryptedToken)
                Log.d(TAG, "Retrieved ID token from the DataStore")
                token
            } else {
                Log.d(TAG, "No ID token found in the DataStore")
                null
            }
        } catch (error: Exception) {
            Log.e(TAG, "Error getting token: ${error.printStackTrace()}")
            return null
        }
    }

    private suspend fun clearToken() {
        dataStoreService.deleteToken()
        Log.d(TAG, "Removed ID token from the DataStore")
    }

    fun createAccount(username: String, password: String) {
        val loginCredentials = LoginCredentials(username, password)

        job = viewModelScope.launch {
            isLoading = true
            val result = chefRepository.createChef(loginCredentials)
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val loginResponse = result.response
                    recipeError = null
                    showAlert = false

                    saveToken(loginResponse.token)
                    chef = Chef(
                        uid = loginResponse.uid,
                        email = username,
                        emailVerified = loginResponse.emailVerified,
                        ratings = mapOf(),
                        recentRecipes = mapOf(),
                        favoriteRecipes = listOf(),
                        token = loginResponse.token
                    )
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun sendVerificationEmail() {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.verifyEmail(token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val emailResponse = result.response
                    recipeError = null
                    showAlert = false

                    // Don't update the chef's verified status until they click the deep link
                    emailResponse.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = token != null && job?.isCancelled == false
                }
            }
        }
    }

    fun resetPassword(email: String) {
        val fields = ChefUpdate(
            type = ChefUpdateType.PASSWORD,
            email = email
        )

        job = viewModelScope.launch {
            isLoading = true
            val result = chefRepository.updateChef(fields)
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    // result.response isn't needed
                    emailSent = true
                    recipeError = null
                    showAlert = false
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun getChef() {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.getChef(token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val chefResponse = result.response
                    chef = chefResponse
                    recipeError = null
                    showAlert = false

                    saveToken(chefResponse.token)
                    authState = if (chefResponse.emailVerified) AuthState.AUTHENTICATED
                        else AuthState.UNAUTHENTICATED
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    // Don't show an alert if the user isn't authenticated
                    showAlert = token != null && job?.isCancelled == false

                    clearToken()
                    authState = AuthState.UNAUTHENTICATED
                }
            }
        }
    }

    fun login(username: String, password: String) {
        val loginCredentials = LoginCredentials(username, password)

        job = viewModelScope.launch {
            isLoading = true
            val result = chefRepository.login(loginCredentials)
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val loginResponse = result.response
                    recipeError = null
                    showAlert = false

                    saveToken(loginResponse.token)
                    chef = Chef(
                        uid = loginResponse.uid,
                        email = username,
                        emailVerified = loginResponse.emailVerified,
                        ratings = mapOf(),
                        recentRecipes = mapOf(),
                        favoriteRecipes = listOf(),
                        token = loginResponse.token
                    )

                    if (loginResponse.emailVerified) {
                        authState = AuthState.AUTHENTICATED
                        openLoginDialog = false
                    }
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun logout() {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.logout(token)
            } else {
                // Assume the user should be signed out since there's no auth token
                ChefResult.Success(null)
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    recipeError = null
                    showAlert = false

                    clearToken()
                    chef = null
                    authState = AuthState.UNAUTHENTICATED
                    openLoginDialog = false
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        val fields = ChefUpdate(
            type = ChefUpdateType.EMAIL,
            email = newEmail
        )

        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.updateChef(fields, token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val updateResponse = result.response
                    emailSent = true
                    recipeError = null
                    showAlert = false

                    updateResponse.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = token != null && job?.isCancelled == false
                }
            }
        }
    }

    fun updatePassword(newPassword: String) {
        val fields = ChefUpdate(
            type = ChefUpdateType.PASSWORD,
            email = chef?.email ?: "",
            password = newPassword
        )

        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.updateChef(fields, token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    val updateResponse = result.response
                    passwordUpdated = true
                    recipeError = null
                    showAlert = false

                    updateResponse.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = token != null && job?.isCancelled == false
                }
            }
        }
    }

    fun deleteAccount() {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val result = if (token != null) {
                chefRepository.deleteChef(token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (result) {
                is ChefResult.Success -> {
                    recipeError = null
                    showAlert = false

                    clearToken()
                    chef = null
                    authState = AuthState.UNAUTHENTICATED
                    accountDeleted = true
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = token != null && job?.isCancelled == false
                }
            }
        }
    }

//    fun getRecipeById(id: Int) {
//        job = viewModelScope.launch {
//            isLoading = true
//            val result = recipeRepository.getRecipeById(id)
//            isLoading = false
//
//            when (result) {
//                is RecipeResult.Success -> {
//                    recipe = result.response
//                    recipeError = null
//                }
//                is RecipeResult.Error -> {
//                    recipe = null
//                    recipeError = result.recipeError
//                }
//            }
//        }
//    }

    fun getAllChefRecipes() {
        if (chef == null) return

        for (id in chef!!.favoriteRecipes) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        favoriteRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }

        for ((id, timestamp) in chef!!.recentRecipes.entries) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        recentRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }

        for ((id, rating) in chef!!.ratings.entries) {
            id.toIntOrNull()?.let { recipeId ->
                viewModelScope.launch {
                    val result = recipeRepository.getRecipeById(recipeId)

                    if (result is RecipeResult.Success) {
                        ratedRecipes += result.response
                    }
                }
                //getRecipeById(recipeId)
            }
        }
    }
}
