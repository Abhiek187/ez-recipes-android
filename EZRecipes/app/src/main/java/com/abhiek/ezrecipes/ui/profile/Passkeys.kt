package com.abhiek.ezrecipes.ui.profile

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.Chef
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.chef.Passkey
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ConfirmationAlert
import com.abhiek.ezrecipes.ui.util.InputAlert
import com.abhiek.ezrecipes.ui.util.PasskeyButton
import com.abhiek.ezrecipes.utils.toDateTime

@Composable
fun Passkeys(
    chef: Chef,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val passkeyManager = PasskeyManager(context)

    var selectedPasskey by remember { mutableStateOf<Passkey?>(null) }
    var showPasskeyRenameAlert by remember { mutableStateOf(false) }
    var showPasskeyDeleteConfirmation by remember { mutableStateOf(false) }

    val dismissAlert = {
        selectedPasskey = null
        showPasskeyRenameAlert = false
        showPasskeyDeleteConfirmation = false
    }

    LaunchedEffect(Unit) {
        passkeyManager.syncPasskeysWithServer(
            ids = chef.passkeys.map { it.id },
            userId = chef.uid
        )
        // In case the chef signed in after updating their email
        passkeyManager.updateUsername(
            username = chef.email,
            userId = chef.uid
        )
    }
    LaunchedEffect(profileViewModel.passkeyCreated) {
        if (profileViewModel.passkeyCreated) {
            Toast.makeText(
                context,
                resources.getString(R.string.passkey_created),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.passkeyCreated = false
        }
    }
    LaunchedEffect(profileViewModel.passkeyDeleted) {
        if (profileViewModel.passkeyDeleted) {
            Toast.makeText(
                context,
                resources.getString(R.string.passkey_deleted),
                Toast.LENGTH_SHORT
            ).show()
            profileViewModel.passkeyDeleted = false
        }
    }

    Text(
        text = stringResource(R.string.passkey_title),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.fillMaxWidth()
    )
    chef.passkeys.forEach { passkey ->
        key(passkey.id) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = if (isSystemInDarkTheme()) passkey.iconDark else passkey.iconLight,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = passkey.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = {
                            selectedPasskey = passkey
                            showPasskeyRenameAlert = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.passkey_rename)
                        )
                    }
                    IconButton(
                        onClick = {
                            selectedPasskey = passkey
                            showPasskeyDeleteConfirmation = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.passkey_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = stringResource(
                        R.string.last_used,
                        passkey.lastUsed.toDateTime()
                    )
                )
            }
        }
    }
    PasskeyButton(
        text = stringResource(R.string.passkey_create),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 8.dp),
        onClick = {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.passkey_unsupported),
                    Toast.LENGTH_LONG
                ).show()
                return@PasskeyButton
            }

            profileViewModel.createNewPasskey()
        }
    )

    selectedPasskey?.let { passkey ->
        if (showPasskeyRenameAlert) {
            InputAlert(
                inputLabel = stringResource(R.string.passkey_rename),
                initialInput = passkey.name,
                onConfirm = { newName ->
                    profileViewModel.renamePasskey(passkey.id, newName)
                },
                onDismiss = dismissAlert
            )
        } else if (showPasskeyDeleteConfirmation) {
            ConfirmationAlert(
                message = stringResource(
                    R.string.passkey_delete_confirmation,
                    passkey.name
                ),
                onConfirm = {
                    profileViewModel.deletePasskey(passkey.id)
                },
                onDismiss = dismissAlert
            )
        }
    }
}

private data class PasskeysState(
    val isLoading: Boolean = false,
    val passkeyDeleted: Boolean = false
)

private class PasskeysPreviewParameterProvider : PreviewParameterProvider<PasskeysState> {
    override val values = sequenceOf(
        PasskeysState(),
        PasskeysState(isLoading = true),
        PasskeysState(passkeyDeleted = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun PasskeysPreview(
    @PreviewParameter(PasskeysPreviewParameterProvider::class) state: PasskeysState
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
    profileViewModel.passkeyDeleted = state.passkeyDeleted

    EZRecipesTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Passkeys(chefService.chef, profileViewModel)
            }
        }
    }
}
