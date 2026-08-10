package com.abhiek.ezrecipes.ui.appfunctions

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appfunctions.*
import com.abhiek.ezrecipes.data.recipe.*
import com.abhiek.ezrecipes.data.storage.AppDatabase
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.data.terms.TermsService
import com.abhiek.ezrecipes.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
To test (API 36.1+):
- Build and run the app (so the app functions are saved within the APK)
- adb shell cmd app_function list-app-functions | grep --after-context 10 com.abhiek.ezrecipes
- adb shell cmd app_function execute-app-function \
  --package com.abhiek.ezrecipes \
  --function 'com.abhiek.ezrecipes.ui.appfunctions.BaseEZRecipesAppFunctionService#getRecipeDefinition' \
  --parameters '{"word":"blanch"}'
 - adb shell 'cmd app_function execute-app-function \
  --package com.abhiek.ezrecipes
  --function "com.abhiek.ezrecipes.ui.appfunctions.BaseEZRecipesAppFunctionService#searchRecipes"
  --parameters "{\"filter\":{\"query\":\"pizza\",\"maxCals\":800,\"vegetarian\":true,\"glutenFree\":true,\"type\":[\"main course\",\"lunch\",\"hor d'\''oeuvre\"],\"culture\":[\"Indian\",\"Mexican\",\"Latin American\",\"bbq\"]}}"'
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    // Must match the service name in AndroidManifest.xml
    serviceName = "EZRecipesAppFunctionService",
    // Must match "android.app.appfunctions.v2" in AndroidManifest.xml
    appFunctionXmlFileName = "ez_recipes_app_function_service",
)
abstract class BaseEZRecipesAppFunctionService: AppFunctionService() {
    private val recipeRepository by lazy {
        RecipeRepository(
            recipeService = RecipeService.getInstance(applicationContext),
            recentRecipeDao = AppDatabase.getInstance(applicationContext).recentRecipeDao()
        )
    }
    private val termsRepository by lazy {
        TermsRepository(
            termsService = TermsService.getInstance(applicationContext),
            dataStoreService = DataStoreService(applicationContext)
        )
    }

    companion object {
        private const val TAG = "EZRecipesAppFunctionService"
    }

    // The abstract class can't override onExecuteFunction
    private fun logAppFunction(method: String, vararg args: Pair<String, Any?>) {
        Log.d(TAG, "Calling AppFunction $method with args: ${
            args.joinToString { "${it.first}=${it.second}" }
        }")
    }

    /**
     * Searches for recipes using various filters
     * @param filter Filters for searching recipes, at least one filter must be provided
     * @return A list of basic recipe information,
     * use [getRecipeDetails] to get more detailed information about a specific recipe
     * @throws AppFunctionInvalidArgumentException if any of the filters provided are invalid
     * @throws AppFunctionAppUnknownException if an error occurred while searching for recipes
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchRecipes(
        filter: AgentRecipeFilter
    ): List<AgentRecipePreview> = withContext(Dispatchers.IO) {
        logAppFunction("searchRecipes", "filter" to filter)
        // Validate all the filters provided
        if (filter == AgentRecipeFilter()) {
            throw AppFunctionInvalidArgumentException("At least one filter must be provided")
        }
        if (filter.query?.isBlank() == true) {
            throw AppFunctionInvalidArgumentException("Query cannot be blank")
        }
        filter.minCals?.let { minCals ->
            if (minCals < Constants.MIN_CALS || minCals > Constants.MAX_CALS) {
                throw AppFunctionInvalidArgumentException(
                    "Minimum calories must be between ${Constants.MIN_CALS} and ${Constants.MAX_CALS}"
                )
            }
        }
        filter.maxCals?.let { maxCals ->
            if (maxCals < Constants.MIN_CALS || maxCals > Constants.MAX_CALS) {
                throw AppFunctionInvalidArgumentException(
                    "Maximum calories must be between ${Constants.MIN_CALS} and ${Constants.MAX_CALS}"
                )
            }
        }
        filter.rating?.let { rating ->
            if (rating !in 1..5) {
                throw AppFunctionInvalidArgumentException(
                    "Rating must be between 1 and 5"
                )
            }
        }

        val recipeFilter = filter.toRecipeFilter()

        when (val result = recipeRepository.getRecipesByFilter(recipeFilter)) {
            is RecipeResult.Success -> {
                // Return limited recipe information, similar to viewing recipe cards
                return@withContext result.response.map { recipe ->
                    recipe.toAgentRecipePreview()
                }
            }
            is RecipeResult.Error -> {
                val errorMessage = "Failed to search recipes :: ${result.recipeError}"
                Log.e(TAG, errorMessage)
                throw AppFunctionAppUnknownException(errorMessage)
            }
        }
    }

    /**
     * Gets recipe details by ID
     * @param id The ID of the recipe
     * @return recipe information
     * @throws AppFunctionInvalidArgumentException if [id] is negative
     * @throws AppFunctionElementNotFoundException if the recipe couldn't be found
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getRecipeDetails(id: Int): AgentRecipe = withContext(Dispatchers.IO) {
        logAppFunction("getRecipeDetails", "id" to id)
        if (id < 0) {
            throw AppFunctionInvalidArgumentException("Recipe ID cannot be negative")
        }

        when (val result = recipeRepository.getRecipeById(id)) {
            is RecipeResult.Success -> {
                // Only return fields useful to an LLM to save on context & reduce hallucinations
                return@withContext result.response.toAgentRecipe()
            }
            is RecipeResult.Error -> {
                Log.e(TAG, "Failed to get recipe details :: ${result.recipeError}")
                throw AppFunctionElementNotFoundException("Recipe with ID $id could not be found")
            }
        }
    }

    /**
     * Gets the definition of a cooking-related term
     * @param word The term to look up
     * @return The definition of [word], if found
     * @throws AppFunctionInvalidArgumentException if [word] is blank
     * @throws AppFunctionElementNotFoundException if no definition is found for [word]
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getRecipeDefinition(word: String): String = withContext(Dispatchers.IO) {
        logAppFunction("getRecipeDefinition", "word" to word)
        if (word.isBlank()) {
            throw AppFunctionInvalidArgumentException("Word cannot be blank")
        }

        val terms = termsRepository.getTerms()
        val definition = terms.find { it.word == word }?.definition

        if (definition == null) {
            throw AppFunctionElementNotFoundException("No definition found for word $word")
        } else {
            return@withContext definition
        }
    }
}
