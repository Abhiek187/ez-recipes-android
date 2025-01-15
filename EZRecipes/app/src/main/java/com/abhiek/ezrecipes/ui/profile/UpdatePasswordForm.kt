package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.*
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
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun UpdatePasswordForm(profileViewModel: ProfileViewModel, onDismiss: () -> Unit = {}) {
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Focus
    var passwordTouched by remember { mutableStateOf(false) }
    var passwordConfirmTouched by remember { mutableStateOf(false) }

    // Errors
    val passwordEmpty = password.isEmpty()
    val passwordTooShort = password.length < Constants.PASSWORD_MIN_LENGTH
    val passwordsDoNotMatch = password != passwordConfirm

    LaunchedEffect(profileViewModel.passwordUpdated) {
        if (profileViewModel.passwordUpdated) {
            onDismiss()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(stringResource(R.string.change_password_field))
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) {
                            stringResource(R.string.password_hide)
                        } else {
                            stringResource(R.string.password_show)
                        }
                    )
                }
            },
            supportingText = {
                if (passwordTouched && passwordEmpty) {
                    Text(stringResource(R.string.field_required, "Password"))
                } else if (passwordTouched && passwordTooShort) {
                    Text(stringResource(R.string.password_min_length))
                }
            },
            isError = passwordTouched && (passwordEmpty || passwordTooShort),
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.onFocusChanged {
                if (it.isFocused) passwordTouched = true
            }
        )
        TextField(
            value = passwordConfirm,
            onValueChange = {
                passwordConfirm = it
            },
            label = {
                Text(stringResource(R.string.password_confirm_field))
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) {
                            stringResource(R.string.password_hide)
                        } else {
                            stringResource(R.string.password_show)
                        }
                    )
                }
            },
            supportingText = {
                if (passwordConfirmTouched && passwordsDoNotMatch) {
                    Text(stringResource(R.string.password_match))
                } else {
                    Text(stringResource(R.string.password_min_length))
                }
            },
            isError = passwordConfirmTouched && passwordsDoNotMatch,
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.onFocusChanged {
                if (it.isFocused) passwordConfirmTouched = true
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
                    profileViewModel.updatePassword(password)
                },
                enabled = !passwordEmpty && !passwordTooShort && !passwordsDoNotMatch &&
                        !profileViewModel.isLoading
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
    }
}

private data class UpdatePasswordDialogState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class UpdatePasswordDialogPreviewParameterProvider :
    PreviewParameterProvider<UpdatePasswordDialogState> {
    override val values = sequenceOf(
        UpdatePasswordDialogState(isLoading = false, showAlert = false),
        UpdatePasswordDialogState(isLoading = true, showAlert = false),
        UpdatePasswordDialogState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun UpdatePasswordDialogPreview(
    @PreviewParameter(UpdatePasswordDialogPreviewParameterProvider::class)
    state: UpdatePasswordDialogState
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

    EZRecipesTheme {
        Surface {
            UpdatePasswordForm(profileViewModel)
        }
    }
}
