package com.abhiek.ezrecipes.ui.login

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.boldAnnotatedString

@Composable
fun ForgotPasswordForm() {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    val emailEmpty = email.isEmpty()
    val emailInvalid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        if (!emailSent) {
            Text(
                text = stringResource(R.string.forget_password_header),
                style = MaterialTheme.typography.headlineSmall
            )
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
                    imeAction = ImeAction.Done
                )
            )
            Button(
                onClick = {
                    emailSent = true
                },
                enabled = !emailEmpty && !emailInvalid,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.forget_password_submit))
            }
        } else {
            Text(
                text = boldAnnotatedString(
                    text = stringResource(R.string.forget_password_confirm, email),
                    startIndex = 19,
                    endIndex = 20 + email.length
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ForgotPasswordFormPreview() {
    EZRecipesTheme {
        Surface {
            ForgotPasswordForm()
        }
    }
}
