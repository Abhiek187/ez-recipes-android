package com.abhiek.ezrecipes.ui.profile

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.abhiek.ezrecipes.utils.boldAnnotatedString

@Composable
fun UpdateEmailForm(profileViewModel: ProfileViewModel) {
    var email by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }

    val emailEmpty = email.isEmpty()
    val emailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()

    LaunchedEffect(Unit) {
        profileViewModel.emailSent = false
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        if (!profileViewModel.emailSent) {
            TextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(stringResource(R.string.change_email_field))
                },
                supportingText = {
                    if (emailTouched && emailEmpty) {
                        Text(stringResource(R.string.field_required, "Email"))
                    } else if (emailTouched && emailInvalid) {
                        Text(stringResource(R.string.email_invalid))
                    }
                },
                isError = emailTouched && (emailEmpty || emailInvalid),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .semantics { contentType = ContentType.EmailAddress }
                    .onFocusChanged {
                        if (it.isFocused) emailTouched = true
                    }
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
                        profileViewModel.updateEmail(email)
                    },
                    enabled = !emailEmpty && !emailInvalid && !profileViewModel.isLoading
                ) {
                    Text(stringResource(R.string.submit_button))
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
        } else {
            Text(
                text = boldAnnotatedString(
                    text = stringResource(R.string.change_email_confirm, email),
                    startIndex = 19,
                    endIndex = 20 + email.length
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private data class UpdateEmailDialogState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class UpdateEmailDialogPreviewParameterProvider :
    PreviewParameterProvider<UpdateEmailDialogState> {
    override val values = sequenceOf(
        UpdateEmailDialogState(isLoading = false, showAlert = false),
        UpdateEmailDialogState(isLoading = true, showAlert = false),
        UpdateEmailDialogState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun UpdateEmailDialogPreview(
    @PreviewParameter(UpdateEmailDialogPreviewParameterProvider::class)
    state: UpdateEmailDialogState
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
    profileViewModel.isLoading = state.isLoading
    profileViewModel.showAlert = state.showAlert

    EZRecipesTheme {
        Surface {
            UpdateEmailForm(profileViewModel)
        }
    }
}
