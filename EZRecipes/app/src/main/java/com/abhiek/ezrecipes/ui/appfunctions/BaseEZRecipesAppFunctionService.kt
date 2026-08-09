package com.abhiek.ezrecipes.ui.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.*
import com.abhiek.ezrecipes.ui.MainViewModel
import com.abhiek.ezrecipes.ui.glossary.GlossaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
App Function ideas:
- Search recipes by filters
- Get more details about a recipe (summarize or show recipe page)
- Ask what a recipe term means
 */

/**
To test:
- adb shell cmd app_function list-app-functions | grep --after-context 10 com.abhiek.ezrecipes
- adb shell cmd app_function execute-app-function \
  --package com.abhiek.ezrecipes \
  --function 'com.abhiek.ezrecipes.ui.appfunctions.BaseEZRecipesAppFunctionService#getDefinition' \
  --parameters '{"word": "blanch"}'
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "EZRecipesAppFunctionService",
    appFunctionXmlFileName = "ez_recipes_app_function_service",
)
abstract class BaseEZRecipesAppFunctionService: AppFunctionService() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var glossaryViewModel: GlossaryViewModel

    /**
     * Gets the definition of a cooking-related term
     * @param word The term to look up
     * @return The definition of [word], if found
     * @throws AppFunctionInvalidArgumentException if [word] is blank
     * @throws AppFunctionElementNotFoundException if no definition is found for [word]
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getDefinition(word: String): String = withContext(Dispatchers.IO) {
        if (word.isBlank()) {
            throw AppFunctionInvalidArgumentException("Word cannot be blank")
        }
        glossaryViewModel.checkCachedTerms()

        val definition = glossaryViewModel.terms.find { it.word == word }?.definition
        if (definition == null) {
            throw AppFunctionElementNotFoundException("No definition found for word $word")
        } else {
            return@withContext definition
        }
    }
}
