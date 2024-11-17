package com.abhiek.ezrecipes.ui.login

import android.util.Patterns
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ErrorAlert
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun SignUpForm(
    profileViewModel: ProfileViewModel,
    onLogin: () -> Unit,
    onVerifyEmail: (email: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Errors
    val emailEmpty = email.isEmpty()
    val emailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordEmpty = password.isEmpty()
    val passwordTooShort = password.length < Constants.PASSWORD_MIN_LENGTH
    val passwordsDoNotMatch = password != passwordConfirm

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.sign_up_header),
            style = MaterialTheme.typography.headlineLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sign_up_sub_header),
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                onClick = {
                    onVerifyEmail(email)
                }
            ) {
                Text(
                    text = stringResource(R.string.sign_in_header),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(stringResource(R.string.email_field))
            },
            supportingText = {
                if (emailEmpty) {
                    Text(stringResource(R.string.field_required, "Email"))
                } else if (emailInvalid) {
                    Text(stringResource(R.string.email_invalid))
                }
            },
            isError = emailEmpty || emailInvalid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(stringResource(R.string.password_field))
            },
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = if (showPassword) "Hide password"
                        else "Show password"
                    )
                }
            },
            supportingText = {
                if (passwordEmpty) {
                    Text(stringResource(R.string.field_required, "Password"))
                } else if (passwordTooShort) {
                    Text(stringResource(R.string.password_min_length))
                }
            },
            isError = passwordEmpty || passwordTooShort,
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
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
                        contentDescription = if (showPassword) "Hide password"
                        else "Show password"
                    )
                }
            },
            supportingText = {
                if (passwordsDoNotMatch) {
                    Text(stringResource(R.string.password_match))
                } else {
                    Text(stringResource(R.string.password_min_length))
                }
            },
            isError = passwordsDoNotMatch,
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
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
                    profileViewModel.createAccount(email, password)
                    profileViewModel.verifyEmail()
                    onLogin()
                },
                enabled = !emailEmpty && !emailInvalid && !passwordEmpty && !passwordTooShort &&
                        !passwordsDoNotMatch
            ) {
                Text(stringResource(R.string.sign_up_header))
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

private data class SignUpFormState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class SignUpFormPreviewParameterProvider: PreviewParameterProvider<SignUpFormState> {
    // Show previews of the default home screen, with the progress bar, and with an alert
    override val values = sequenceOf(
        SignUpFormState(isLoading = false, showAlert = false),
        SignUpFormState(isLoading = true, showAlert = false),
        SignUpFormState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SignUpFormPreview(
    @PreviewParameter(SignUpFormPreviewParameterProvider::class) state: SignUpFormState
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
            SignUpForm(profileViewModel, {}, {})
        }
    }
}
