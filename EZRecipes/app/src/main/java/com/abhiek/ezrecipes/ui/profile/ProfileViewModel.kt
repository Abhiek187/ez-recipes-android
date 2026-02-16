package com.abhiek.ezrecipes.ui.profile

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCancellationException
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
import com.abhiek.ezrecipes.utils.toISODateString
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val chefRepository: ChefRepository,
    private val recipeRepository: RecipeRepository,
    private val dataStoreService: DataStoreService,
    private val passkeyManager: PasskeyManager
): ViewModel() {
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)

    var authState by mutableStateOf(AuthState.LOADING)
    var isLoading by mutableStateOf(false)
    var chef by mutableStateOf<Chef?>(null)
    var openLoginDialog by mutableStateOf(false)
    var showAlert by mutableStateOf(false)
    var emailSent by mutableStateOf(false)
    var passwordUpdated by mutableStateOf(false)
    var accountDeleted by mutableStateOf(false)
    var authUrls by mutableStateOf<Map<Provider, Uri>>(mapOf())
        private set
    // "" = auth code not used, null = auth code missing
    var authCode by mutableStateOf<String?>("")
    var provider by mutableStateOf<Provider?>(null)
    var accountLinked by mutableStateOf(false)
    var accountUnlinked by mutableStateOf(false)
    var passkeyDeleted by mutableStateOf(false)

    private val _favoriteRecipes = MutableStateFlow(listOf<Recipe?>())
    private val _recentRecipes = MutableStateFlow(listOf<Recipe?>())
    private val _ratedRecipes = MutableStateFlow(listOf<Recipe?>())
    val favoriteRecipes = _favoriteRecipes.asStateFlow()
    val recentRecipes = _recentRecipes.asStateFlow()
    val ratedRecipes = _ratedRecipes.asStateFlow()

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
            Log.e(TAG, "Error saving token: $error")
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
            Log.e(TAG, "Error getting token: $error")
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
                        providerData = listOf(),
                        passkeys = listOf(),
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

            when (val loginResult = chefRepository.login(loginCredentials)) {
                is ChefResult.Success -> {
                    val loginResponse = loginResult.response
                    recipeError = null
                    showAlert = false

                    saveToken(loginResponse.token)
                    chef = Chef(
                        uid = loginResponse.uid,
                        email = username,
                        emailVerified = loginResponse.emailVerified,
                        providerData = listOf(),
                        passkeys = listOf(),
                        ratings = mapOf(),
                        recentRecipes = mapOf(),
                        favoriteRecipes = listOf(),
                        token = loginResponse.token
                    )

                    // Fetch the rest of the chef's profile
                    val chefResult = chefRepository.getChef(loginResponse.token)
                    isLoading = false

                    when (chefResult) {
                        is ChefResult.Success -> {
                            val chefResponse = chefResult.response
                            chef = chefResponse
                        }
                        is ChefResult.Error -> {
                            recipeError = chefResult.recipeError
                            showAlert = job?.isCancelled == false
                        }
                    }

                    if (loginResponse.emailVerified) {
                        authState = AuthState.AUTHENTICATED
                        openLoginDialog = false
                    }
                }
                is ChefResult.Error -> {
                    isLoading = false
                    recipeError = loginResult.recipeError
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

    fun getAuthUrls() {
        job = viewModelScope.launch {
            when (val result = chefRepository.getAuthUrls(Constants.REDIRECT_URL)) {
                is ChefResult.Success -> {
                    recipeError = null
                    showAlert = false
                    // Convert the list to a map
                    authUrls = result.response.associate {
                        it.providerId to it.authUrl.toUri()
                    }
                }
                is ChefResult.Error -> {
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false

                    authUrls = mapOf()
                }
            }
        }
    }

    fun loginWithOAuth(code: String, provider: Provider) {
        val oAuthRequest = OAuthRequest(
            code = code,
            providerId = provider.toString(),
            redirectUrl = Constants.REDIRECT_URL
        )

        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()

            when (val result = chefRepository.loginWithOAuth(oAuthRequest, token)) {
                is ChefResult.Success -> {
                    val loginResponse = result.response
                    recipeError = null
                    showAlert = false

                    saveToken(loginResponse.token)
                    if (chef == null) {
                        // The email will be gotten from the GET chef response
                        chef = Chef(
                            uid = loginResponse.uid,
                            email = "",
                            emailVerified = loginResponse.emailVerified,
                            providerData = listOf(),
                            passkeys = listOf(),
                            ratings = mapOf(),
                            recentRecipes = mapOf(),
                            favoriteRecipes = listOf(),
                            token = loginResponse.token
                        )
                    }

                    // Fetch the rest of the chef's profile
                    val chefResult = chefRepository.getChef(loginResponse.token)
                    isLoading = false

                    when (chefResult) {
                        is ChefResult.Success -> {
                            chef = chefResult.response
                            accountLinked = token != null
                        }
                        is ChefResult.Error -> {
                            recipeError = chefResult.recipeError
                            showAlert = job?.isCancelled == false
                        }
                    }

                    if (loginResponse.emailVerified) {
                        authState = AuthState.AUTHENTICATED
                        openLoginDialog = false
                    }
                }
                is ChefResult.Error -> {
                    isLoading = false
                    recipeError = result.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun unlinkOAuthProvider(provider: Provider) {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val unlinkResult = if (token != null) {
                chefRepository.unlinkOAuthProvider(provider, token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }

            when (unlinkResult) {
                is ChefResult.Success -> {
                    val tokenResponse = unlinkResult.response
                    recipeError = null
                    showAlert = false

                    val newToken = tokenResponse.token
                    if (newToken == null) {
                        isLoading = false
                        return@launch
                    }
                    saveToken(newToken)

                    // Get the chef's updated provider data
                    val chefResult = chefRepository.getChef(newToken)
                    isLoading = false

                    when (chefResult) {
                        is ChefResult.Success -> {
                            chef = chefResult.response
                            accountUnlinked = true
                        }
                        is ChefResult.Error -> {
                            recipeError = chefResult.recipeError
                            showAlert = job?.isCancelled == false
                        }
                    }
                }
                is ChefResult.Error -> {
                    isLoading = false
                    recipeError = unlinkResult.recipeError
                    showAlert = token != null && job?.isCancelled == false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun loginWithPasskey(email: String) {
        job = viewModelScope.launch {
            isLoading = true
            val passkeyOptionsResult = chefRepository.getExistingPasskeyChallenge(email)
            isLoading = false

            when (passkeyOptionsResult) {
                is ChefResult.Success -> {
                    val serverPasskeyOptions = passkeyOptionsResult.response

                    try {
                        val serverPasskeyResponse = passkeyManager.getPasskey(
                            serverPasskeyOptions
                        )
                        isLoading = true
                        val passkeyValidateResult = chefRepository.validateExistingPasskey(
                            serverPasskeyResponse,
                            email
                        )
                        isLoading = false

                        when (passkeyValidateResult) {
                            is ChefResult.Success -> {
                                recipeError = null
                                showAlert = false

                                val newToken = passkeyValidateResult.response.token
                                isLoading = true
                                val chefResult = if (newToken != null) {
                                    saveToken(newToken)
                                    chefRepository.getChef(newToken)
                                } else {
                                    ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
                                }
                                isLoading = false

                                when (chefResult) {
                                    is ChefResult.Success -> {
                                        chef = chefResult.response
                                    }
                                    is ChefResult.Error -> {
                                        recipeError = chefResult.recipeError
                                        showAlert = job?.isCancelled == false
                                    }
                                }

                                authState = AuthState.AUTHENTICATED
                                openLoginDialog = false
                            }
                            is ChefResult.Error -> {
                                recipeError = passkeyValidateResult.recipeError
                                showAlert = job?.isCancelled == false
                            }
                        }
                    } catch (error: Exception) {
                        println(error.printStackTrace())
                        Log.e(TAG, "Error signing in with a passkey: $error")

                        // Don't show an error if the user dismissed the passkey prompt
                        if (error !is GetCredentialCancellationException) {
                            recipeError =
                                RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
                            showAlert = job?.isCancelled == false
                        }
                    }
                }
                is ChefResult.Error -> {
                    recipeError = passkeyOptionsResult.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun createNewPasskey() {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val passkeyOptionsResult = if (token != null) {
                chefRepository.getNewPasskeyChallenge(token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }
            isLoading = false

            when (passkeyOptionsResult) {
                is ChefResult.Success -> {
                    val serverPasskeyOptions = passkeyOptionsResult.response

                    try {
                        val serverPasskeyResponse = passkeyManager.createPasskey(
                            serverPasskeyOptions
                        )
                        isLoading = true
                        val passkeyValidateResult = chefRepository.validateNewPasskey(
                            serverPasskeyResponse,
                            token!!
                        )
                        isLoading = false

                        when (passkeyValidateResult) {
                            is ChefResult.Success -> {
                                recipeError = null
                                showAlert = false

                                val newToken = passkeyValidateResult.response.token
                                isLoading = true
                                val chefResult = if (newToken != null) {
                                    saveToken(newToken)
                                    chefRepository.getChef(newToken)
                                } else {
                                    ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
                                }
                                isLoading = false

                                when (chefResult) {
                                    is ChefResult.Success -> {
                                        chef = chefResult.response
                                    }
                                    is ChefResult.Error -> {
                                        recipeError = chefResult.recipeError
                                        showAlert = job?.isCancelled == false
                                    }
                                }
                            }
                            is ChefResult.Error -> {
                                recipeError = passkeyValidateResult.recipeError
                                showAlert = job?.isCancelled == false
                            }
                        }
                    } catch (error: Exception) {
                        // Handling create credential exceptions:
                        // https://developer.android.com/identity/passkeys/create-passkeys#handle-response
                        Log.e(TAG, "Error creating a new passkey: $error")

                        if (error !is CreateCredentialCancellationException) {
                            recipeError =
                                RecipeError(error.localizedMessage ?: Constants.UNKNOWN_ERROR)
                            showAlert = job?.isCancelled == false
                        }
                    }
                }
                is ChefResult.Error -> {
                    recipeError = passkeyOptionsResult.recipeError
                    showAlert = job?.isCancelled == false
                }
            }
        }
    }

    fun deletePasskey(id: String) {
        job = viewModelScope.launch {
            isLoading = true
            val token = getToken()
            val deletePasskeyResult = if (token != null) {
                chefRepository.deletePasskey(id, token)
            } else {
                ChefResult.Error(RecipeError(Constants.NO_TOKEN_FOUND))
            }

            when (deletePasskeyResult) {
                is ChefResult.Success -> {
                    val tokenResponse = deletePasskeyResult.response
                    recipeError = null
                    showAlert = false

                    val newToken = tokenResponse.token
                    if (newToken == null) {
                        isLoading = false
                        return@launch
                    }
                    saveToken(newToken)

                    // Get the chef's updated passkey list
                    val chefResult = chefRepository.getChef(newToken)
                    isLoading = false

                    when (chefResult) {
                        is ChefResult.Success -> {
                            chef = chefResult.response
                            passkeyDeleted = true
                        }
                        is ChefResult.Error -> {
                            recipeError = chefResult.recipeError
                            showAlert = job?.isCancelled == false
                        }
                    }
                }
                is ChefResult.Error -> {
                    isLoading = false
                    recipeError = deletePasskeyResult.recipeError
                    showAlert = token != null && job?.isCancelled == false
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
                    if (result.recipeError.error.contains(
                            Constants.CREDENTIAL_TOO_OLD_ERROR
                    )) {
                        // Prompt the user to sign in again
                        recipeError = null
                        showAlert = false
                        openLoginDialog = true
                    } else {
                        recipeError = result.recipeError
                        showAlert = token != null && job?.isCancelled == false
                    }
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
                    // result.response isn't needed
                    passwordUpdated = true
                    recipeError = null
                    showAlert = false

                    // The token will be revoked, so sign out the user
                    clearToken()
                    authState = AuthState.UNAUTHENTICATED
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

    fun getAllFavoriteRecipes() {
        val chef = this.chef ?: return

        viewModelScope.launch {
            val recipeIds = chef.favoriteRecipes.mapNotNull { it.toIntOrNull() }

            // null = loading
            val initialRecipes = recipeIds.map { null }
            _favoriteRecipes.update { initialRecipes }

            // Use coroutineScope to ensure all child coroutines complete before exiting
            coroutineScope {
                // Fetch all recipes in parallel
                val jobs = recipeIds.mapIndexed { index, recipeId ->
                    async {
                        when (val result = recipeRepository.getRecipeById(recipeId)) {
                            is RecipeResult.Success -> {
                                // Update the state for this specific recipe
                                _favoriteRecipes.update { currentState ->
                                    currentState.toMutableList().apply {
                                        this[index] = result.response
                                    }
                                }
                            }
                            is RecipeResult.Error -> {
                                Log.w(TAG, "Failed to get recipe $recipeId :: error: ${
                                    result.recipeError.error
                                }")
                            }
                        }
                    }
                }

                // Remove all recipes that failed to load
                jobs.awaitAll()
                _favoriteRecipes.update { currentState ->
                    currentState.filterNotNull()
                }
            }
        }
    }

    fun getAllRecentRecipes() {
        val chef = this.chef ?: return

        viewModelScope.launch {
            // Sort the recipe IDs by most recent timestamp
            val recipeIds = chef.recentRecipes
                .mapNotNull { (id, timestamp) -> id.toIntOrNull() to timestamp }
                .sortedByDescending { it.second }
                .mapNotNull { it.first }

            val initialRecipes = recipeIds.map { null }
            _recentRecipes.update { initialRecipes }

            coroutineScope {
                // Fetch all recipes in parallel
                val jobs = recipeIds.mapIndexed { index, recipeId ->
                    async {
                        when (val result = recipeRepository.getRecipeById(recipeId)) {
                            is RecipeResult.Success -> {
                                _recentRecipes.update { currentState ->
                                    currentState.toMutableList().apply {
                                        this[index] = result.response
                                    }
                                }
                            }
                            is RecipeResult.Error -> {
                                Log.w(TAG, "Failed to get recipe $recipeId :: error: ${
                                    result.recipeError.error
                                }")
                            }
                        }
                    }
                }

                jobs.awaitAll()
                _recentRecipes.update { currentState ->
                    currentState.filterNotNull()
                }
            }
        }
    }

    fun getAllRatedRecipes() {
        val chef = this.chef ?: return

        viewModelScope.launch {
            val recipeIds = chef.ratings.mapNotNull { (id, _) -> id.toIntOrNull() }

            val initialRecipes = recipeIds.map { null }
            _ratedRecipes.update { initialRecipes }

            coroutineScope {
                val jobs = recipeIds.mapIndexed { index, recipeId ->
                    async {
                        when (val result = recipeRepository.getRecipeById(recipeId)) {
                            is RecipeResult.Success -> {
                                _ratedRecipes.update { currentState ->
                                    currentState.toMutableList().apply {
                                        this[index] = result.response
                                    }
                                }
                            }
                            is RecipeResult.Error -> {
                                Log.w(TAG, "Failed to get recipe $recipeId :: error: ${
                                    result.recipeError.error
                                }")
                            }
                        }
                    }
                }

                jobs.awaitAll()
                _ratedRecipes.update { currentState ->
                    currentState.filterNotNull()
                }
            }
        }
    }

    fun updateRecipeViews(recipe: Recipe) {
        viewModelScope.launch {
            // Recipe view updates can occur in the background without impacting the UX
            dataStoreService.incrementRecipesViewed()

            val recipeViewUpdate = RecipeUpdate(view = true)
            val token = getToken()
            val result = recipeRepository.updateRecipe(
                id = recipe.id,
                fields = recipeViewUpdate,
                token = token
            )

            when (result) {
                is RecipeResult.Success -> {
                    Log.d(TAG, "Recipe view count updated successfully")
                    val currentDate = System.currentTimeMillis().toISODateString()
                    chef = chef?.copy(
                        recentRecipes = chef!!.recentRecipes + (recipe.id.toString() to currentDate)
                    )
                    recipeRepository.saveRecentRecipe(recipe)

                    result.response.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is RecipeResult.Error -> {
                    Log.w(
                        TAG, "Failed to update the recipe view count :: error: ${
                        result.recipeError.error
                    }")
                }
            }
        }
    }

    fun toggleFavoriteRecipe(recipeId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            isLoading = true
            val recipeUpdate = RecipeUpdate(isFavorite = isFavorite)
            val token = getToken()
            val result = recipeRepository.updateRecipe(recipeId, recipeUpdate, token)
            isLoading = false

            when (result) {
                is RecipeResult.Success -> {
                    chef = chef?.copy(
                        favoriteRecipes = if (isFavorite) {
                            chef!!.favoriteRecipes + recipeId.toString()
                        } else {
                            chef!!.favoriteRecipes - recipeId.toString()
                        }
                    )
                    recipeRepository.toggleFavoriteRecentRecipe(recipeId)

                    result.response.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is RecipeResult.Error -> {
                    Log.w(TAG, "Failed to update the recipe favorite status :: error: ${
                        result.recipeError.error
                    }")
                }
            }
        }
    }

    fun rateRecipe(recipeId: Int, rating: Int) {
        viewModelScope.launch {
            isLoading = true
            val recipeUpdate = RecipeUpdate(rating = rating)
            val token = getToken()
            val result = recipeRepository.updateRecipe(recipeId, recipeUpdate, token)
            isLoading = false

            when (result) {
                is RecipeResult.Success -> {
                    chef = chef?.copy(
                        ratings = chef!!.ratings + (recipeId.toString() to rating)
                    )

                    result.response.token?.let { newToken ->
                        saveToken(newToken)
                    }
                }
                is RecipeResult.Error -> {
                    Log.w(TAG, "Failed to rate the recipe :: error: ${
                        result.recipeError.error
                    }")
                }
            }
        }
    }
}
