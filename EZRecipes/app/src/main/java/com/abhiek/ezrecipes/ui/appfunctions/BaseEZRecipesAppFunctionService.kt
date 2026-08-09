package com.abhiek.ezrecipes.ui.appfunctions

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appfunctions.*
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.data.terms.Term
import com.abhiek.ezrecipes.data.terms.TermsRepository
import com.abhiek.ezrecipes.data.terms.TermsResult
import com.abhiek.ezrecipes.data.terms.TermsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
App Function ideas:
- Search recipes by filters
- Get more details about a recipe (summarize or show recipe page)
- Ask what a recipe term means
 */

/**
To test (API 36.1+):
- Build and run the app (so the app functions are saved within the APK)
- adb shell cmd app_function list-app-functions | grep --after-context 10 com.abhiek.ezrecipes
- adb shell cmd app_function execute-app-function \
  --package com.abhiek.ezrecipes \
  --function 'com.abhiek.ezrecipes.ui.appfunctions.BaseEZRecipesAppFunctionService#getDefinition' \
  --parameters '{"word":"blanch"}'
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    // Must match the service name in AndroidManifest.xml
    serviceName = "EZRecipesAppFunctionService",
    // Must match "android.app.appfunctions.v2" in AndroidManifest.xml
    appFunctionXmlFileName = "ez_recipes_app_function_service",
)
abstract class BaseEZRecipesAppFunctionService: AppFunctionService() {
    private val termsRepository by lazy {
        TermsRepository(
            termsService = TermsService.getInstance(applicationContext)
        )
    }
    private val dataStoreService by lazy {
        DataStoreService(applicationContext)
    }

    companion object {
        private const val TAG = "EZRecipesAppFunctionService"
    }

    // The abstract class can't override onExecuteFunction
    private fun logAppFunction(method: String, vararg args: Pair<String, Any?>) {
        Log.d(TAG, "Calling AppFunction $method with args: ${args.joinToString {
            "${it.first}=${it.second}"
        }
        }")
    }

    /**
     * Gets the definition of a cooking-related term
     * @param word The term to look up
     * @return The definition of [word], if found
     * @throws AppFunctionInvalidArgumentException if [word] is blank
     * @throws AppFunctionElementNotFoundException if no definition is found for [word]
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getDefinition(word: String): String = withContext(Dispatchers.IO) {
        logAppFunction("getDefinition", "word" to word)
        if (word.isBlank()) {
            throw AppFunctionInvalidArgumentException("Word cannot be blank")
        }
        val terms = getTerms()

        val definition = terms.find { it.word == word }?.definition
        if (definition == null) {
            throw AppFunctionElementNotFoundException("No definition found for word $word")
        } else {
            return@withContext definition
        }
    }

    private suspend fun getTerms(): List<Term> {
        // Check if terms need to be cached
        val cachedTerms = dataStoreService.getTerms()
        if (cachedTerms != null) {
            return cachedTerms
        }

        return when (val result = termsRepository.getTerms()) {
            is TermsResult.Success -> {
                dataStoreService.saveTerms(result.response)
                result.response
            }
            is TermsResult.Error -> {
                // No need to handle errors besides logging
                Log.w(TAG, "Failed to get terms :: error: ${result.recipeError}")
                listOf()
            }
        }
    }
}
