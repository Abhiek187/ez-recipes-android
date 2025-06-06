package com.abhiek.ezrecipes.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.*
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
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.ui.util.ErrorAlert

@Composable
fun LoginForm(
    profileViewModel: ProfileViewModel,
    onSignup: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onVerifyEmail: (email: String) -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Focus
    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    // Errors
    val usernameEmpty = username.isEmpty()
    val passwordEmpty = password.isEmpty()

    LaunchedEffect(profileViewModel.chef) {
        // Check if the user signed up, but didn't verify their email yet
        if (profileViewModel.chef?.emailVerified == false) {
            profileViewModel.sendVerificationEmail()
            onVerifyEmail(profileViewModel.chef!!.email)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.sign_in_header),
            style = MaterialTheme.typography.headlineLarge
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.sign_in_sub_header),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f, false)
            )
            TextButton(
                onClick = {
                    onSignup()
                }
            ) {
                Text(
                    text = stringResource(R.string.sign_up_header),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = {
                Text(stringResource(R.string.username_field))
            },
            supportingText = {
                if (usernameTouched && usernameEmpty) {
                    Text(stringResource(R.string.field_required, "Username"))
                }
            },
            isError = usernameTouched && usernameEmpty,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .semantics {
                    contentType = ContentType.Username + ContentType.EmailAddress
                }
                .onFocusChanged {
                    if (it.isFocused) usernameTouched = true
                }
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
                }
            },
            isError = passwordTouched && passwordEmpty,
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .semantics { contentType = ContentType.Password }
                .onFocusChanged {
                    if (it.isFocused) passwordTouched = true
                }
        )
        TextButton(
            onClick = {
                onForgotPassword()
            }
        ) {
            Text(
                text = stringResource(R.string.password_forget),
                style = MaterialTheme.typography.titleLarge
            )
        }
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
                    profileViewModel.login(username, password)
                },
                enabled = !usernameEmpty && !passwordEmpty && !profileViewModel.isLoading,
                modifier = Modifier.testTag("login_dialog_button")
            ) {
                Text(stringResource(R.string.login))
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

private data class LoginFormState(
    val isLoading: Boolean,
    val showAlert: Boolean
)

private class LoginFormPreviewParameterProvider: PreviewParameterProvider<LoginFormState> {
    override val values = sequenceOf(
        LoginFormState(isLoading = false, showAlert = false),
        LoginFormState(isLoading = true, showAlert = false),
        LoginFormState(isLoading = false, showAlert = true)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun LoginFormPreview(
    @PreviewParameter(LoginFormPreviewParameterProvider::class) state: LoginFormState
) {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = viewModel {
        ProfileViewModel(
            chefRepository = ChefRepository(chefService),
            recipeRepository = RecipeRepository(recipeService),
            dataStoreService = DataStoreService(context)
        )
    }
    profileViewModel.isLoading = state.isLoading
    profileViewModel.showAlert = state.showAlert

    EZRecipesTheme {
        Surface {
            LoginForm(profileViewModel)
        }
    }
}
