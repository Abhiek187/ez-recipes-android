package com.abhiek.ezrecipes.ui

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhiek.ezrecipes.BuildConfig
import com.abhiek.ezrecipes.data.models.RecentRecipe
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.recipe.RecipeResult
import com.abhiek.ezrecipes.data.models.Recipe
import com.abhiek.ezrecipes.data.models.RecipeError
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.utils.Constants
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Connects the View to the Repository
class MainViewModel(
    private val recipeRepository: RecipeRepository,
    private val dataStoreService: DataStoreService,
    private val reviewManager: ReviewManager
): ViewModel() {
    // Only expose a read-only copy of the state to the View
    var job by mutableStateOf<Job?>(null)
        private set
    var recipeError by mutableStateOf<RecipeError?>(null)
        private set

    var recipe by mutableStateOf<Recipe?>(null)
    var isLoading by mutableStateOf(false)
    // Alerts the home screen to navigate to the recipe screen
    var isRecipeLoaded by mutableStateOf(false)
    var showRecipeAlert by mutableStateOf(false)
    var recentRecipes by mutableStateOf<List<RecentRecipe>>(listOf())

    private var isFirstPrompt = true

    companion object {
        private const val TAG = "MainViewModel"
        const val CURRENT_VERSION = BuildConfig.VERSION_CODE
    }

    private fun updateRecipeProps(
        result: RecipeResult<Recipe>,
        fromHome: Boolean
    ) {
        // Set all the ViewModel properties based on the API result
        when (result) {
            is RecipeResult.Success -> {
                recipe = result.response
                recipeError = null
                isRecipeLoaded = fromHome
                showRecipeAlert = false
            }
            is RecipeResult.Error -> {
                recipe = null
                recipeError = result.recipeError
                isRecipeLoaded = false
                // Don't show an alert if the request was intentionally cancelled
                showRecipeAlert = job?.isCancelled == false
            }
        }
    }

    fun getRandomRecipe(fromHome: Boolean = false) {
        job = viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRandomRecipe()
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }

    fun getRecipeById(id: Int, fromHome: Boolean = false) {
        job = viewModelScope.launch {
            isLoading = true
            val response = recipeRepository.getRecipeById(id)
            isLoading = false

            updateRecipeProps(response, fromHome)
        }
    }

    fun fetchRecentRecipes() {
        viewModelScope.launch {
            recentRecipes = recipeRepository.fetchRecentRecipes()
        }
    }

    fun saveRecentRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeRepository.saveRecentRecipe(recipe)
        }
    }

    fun presentReviewIfQualified(activity: Activity) {
        viewModelScope.launch {
            // Avoid presenting a review immediately on launch
            if (isFirstPrompt) {
                isFirstPrompt = false
                return@launch
            }

            // If the user viewed enough recipes, ask for a review
            // Only ask once per app version to avoid intimidating the user
            // and quickly reaching the quota
            val recipesViewed = dataStoreService.getRecipesViewed()
            val lastVersionReviewed = dataStoreService.getLastVersionReviewed()
            if (
                recipesViewed < Constants.RECIPES_TO_PRESENT_REVIEW
                || CURRENT_VERSION <= lastVersionReviewed
            ) return@launch
            val request = reviewManager.requestReviewFlow()

            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    Log.d(TAG, "Got the ReviewInfo object: $reviewInfo")
                    // Delay for two seconds to avoid interrupting the person using the app
                    viewModelScope.launch {
                        delay(2000)
                        val flow = reviewManager.launchReviewFlow(activity, reviewInfo)

                        flow.addOnCompleteListener {
                            // The user may or may not have reviewed or was prompted to review
                            Log.d(TAG, "Review flow complete!")
                            viewModelScope.launch {
                                dataStoreService.setLastVersionReviewed(CURRENT_VERSION)
                            }
                        }
                    }
                } else {
                    Log.w(
                        TAG,
                        "Failed to request for a review: ${task.exception?.localizedMessage}"
                    )
                }
            }
        }
    }
}
