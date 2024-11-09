package com.abhiek.ezrecipes.ui.login

import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.Routes

@Composable
fun SignUpForm(navController: NavController) {
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

    val context = LocalContext.current

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
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(
                            navController.currentBackStackEntry?.destination?.route
                                ?: return@navigate
                        ) {
                            inclusive =  true
                        }
                        launchSingleTop = true
                    }
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
                imeAction = ImeAction.Done
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
        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Email: $email, Password: $password",
                    Toast.LENGTH_SHORT
                ).show()
            },
            enabled = !(emailEmpty || emailInvalid || passwordEmpty || passwordTooShort ||
                    passwordsDoNotMatch),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.sign_up_header))
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun SignUpFormPreview() {
    val navController = rememberNavController()

    EZRecipesTheme {
        Surface {
            SignUpForm(navController)
        }
    }
}
