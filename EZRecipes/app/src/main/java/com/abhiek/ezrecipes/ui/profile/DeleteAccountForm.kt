package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ErrorAlert

@Composable
fun DeleteAccountForm(profileViewModel: ProfileViewModel) {
    var username by remember { mutableStateOf("") }
    val usernameMatches = username == profileViewModel.chef?.email

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.delete_account_header),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.delete_account_sub_header)
        )
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text(stringResource(R.string.username_field))
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.End)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .alpha(if (profileViewModel.isLoading) 1f else 0f)
            )
            Button(
                onClick = {
                    profileViewModel.deleteAccount()
                },
                enabled = usernameMatches && !profileViewModel.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete_account))
            }
        }
        if (profileViewModel.showAlert) {
            ErrorAlert(
                message = profileViewModel.recipeError?.error,
                onDismiss = {
                    profileViewModel.showAlert = false
                }
            )
        }
    }
}

private data class DeleteAccountDialogState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class DeleteAccountDialogPreviewParameterProvider :
    PreviewParameterProvider<DeleteAccountDialogState> {
    override val values = sequenceOf(
        DeleteAccountDialogState(isLoading = false, showAlert = false),
        DeleteAccountDialogState(isLoading = true, showAlert = false),
        DeleteAccountDialogState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun DeleteAccountDialogPreview(
    @PreviewParameter(DeleteAccountDialogPreviewParameterProvider::class)
    state: DeleteAccountDialogState
) {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )
    profileViewModel.isLoading = state.isLoading
    profileViewModel.showAlert = state.showAlert
    profileViewModel.chef = chefService.chef

    EZRecipesTheme {
        Surface {
            DeleteAccountForm(profileViewModel)
        }
    }
}
