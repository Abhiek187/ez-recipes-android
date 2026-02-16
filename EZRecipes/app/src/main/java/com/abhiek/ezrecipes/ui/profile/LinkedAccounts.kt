package com.abhiek.ezrecipes.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.data.models.Provider
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ConfirmationAlert
import com.abhiek.ezrecipes.ui.util.OAuthButton

@Composable
fun LinkedAccounts(
    chef: Chef,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    var selectedProvider by remember { mutableStateOf(Provider.GOOGLE) }
    var showUnlinkConfirmation by remember { mutableStateOf(false) }
    val linkedAccounts = remember(chef.providerData) {
        // Start with all the supported providers
        val initialResult = Provider.entries.associateWith { mutableListOf<String>() }
        // A chef can link 0 or more emails with a provider
        chef.providerData.fold(initialResult) { result, providerData ->
            Provider.valueOfOrNull(providerData.providerId)?.let { providerId ->
                result[providerId]?.add(providerData.email)
            }

            return@fold result
        }
    }

    val dismissUnlinkConfirmation = {
        showUnlinkConfirmation = false
    }

    LaunchedEffect(profileViewModel.accountLinked) {
        if (profileViewModel.accountLinked) {
            Toast.makeText(
                context,
                resources.getString(R.string.link_success, selectedProvider),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.accountLinked = false
        }
    }
    LaunchedEffect(Unit) {
        profileViewModel.getAuthUrls()
    }
    LaunchedEffect(profileViewModel.accountUnlinked) {
        if (profileViewModel.accountUnlinked) {
            Toast.makeText(
                context,
                resources.getString(R.string.unlink_success, selectedProvider),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.accountUnlinked = false
        }
    }

    Text(
        text = stringResource(R.string.linked_accounts),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.fillMaxWidth()
    )
    linkedAccounts.entries.forEach { (provider, emails) ->
        key(provider) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OAuthButton(
                    provider = provider,
                    authUrl = profileViewModel.authUrls[provider],
                    profileViewModel = profileViewModel
                )
                Button(
                    onClick = {
                        // Confirm before unlinking
                        selectedProvider = provider
                        showUnlinkConfirmation = true
                    },
                    enabled = emails.isNotEmpty() && !profileViewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.unlink),
                            color = if (emails.isEmpty() || profileViewModel.isLoading) {
                                Color.Unspecified
                            } else {
                                MaterialTheme.colorScheme.onError
                            }
                        )
                        if (profileViewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
            if (emails.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.Green
                    )
                    Text(
                        text = emails.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    if (showUnlinkConfirmation) {
        ConfirmationAlert(
            message = stringResource(
                R.string.unlink_confirmation,
                selectedProvider.toString()
            ),
            onConfirm = {
                profileViewModel.unlinkOAuthProvider(selectedProvider)
            },
            onDismiss = dismissUnlinkConfirmation
        )
    }
}

private data class LinkedAccountsState(
    val isLoading: Boolean = false,
    val accountLinked: Boolean = false,
    val accountUnlinked: Boolean = false
)

private class LinkedAccountsPreviewParameterProvider :
    PreviewParameterProvider<LinkedAccountsState> {
    override val values = sequenceOf(
        LinkedAccountsState(),
        LinkedAccountsState(isLoading = true),
        LinkedAccountsState(accountLinked = true),
        LinkedAccountsState(accountUnlinked = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun LinkedAccountsPreview(
    @PreviewParameter(LinkedAccountsPreviewParameterProvider::class) state: LinkedAccountsState
) {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context),
            passkeyManager = PasskeyManager(context)
        )
    }
    profileViewModel.chef = chefService.chef
    profileViewModel.isLoading = state.isLoading
    profileViewModel.accountLinked = state.accountLinked
    profileViewModel.accountUnlinked = state.accountUnlinked

    EZRecipesTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                LinkedAccounts(chefService.chef, profileViewModel)
            }
        }
    }
}
