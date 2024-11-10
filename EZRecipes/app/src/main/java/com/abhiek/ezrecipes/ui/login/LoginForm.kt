package com.abhiek.ezrecipes.ui.login

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
import com.abhiek.ezrecipes.utils.Routes

@Composable
fun LoginForm(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Errors
    val usernameEmpty = username.isEmpty()
    val passwordEmpty = password.isEmpty()

    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
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
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(
                onClick = {
                    navController.navigate(Routes.SIGN_UP) {
                        // Close the modal whenever the user navigates back
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
                if (usernameEmpty) {
                    Text(stringResource(R.string.field_required, "Username"))
                }
            },
            isError = usernameEmpty,
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
                }
            },
            isError = passwordEmpty,
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        TextButton(
            onClick = {
                println("Forgot password")
            }
        ) {
            Text(
                text = stringResource(R.string.password_forget),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Button(
            onClick = {
                Toast.makeText(
                    context,
                    "Username: $username, Password: $password",
                    Toast.LENGTH_SHORT
                ).show()
            },
            enabled = !usernameEmpty && !passwordEmpty,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.login))
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun LoginFormPreview() {
    val navController = rememberNavController()

    EZRecipesTheme {
        Surface {
            LoginForm(navController)
        }
    }
}
