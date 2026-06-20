package com.abhiek.ezrecipes.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.boldAnnotatedString
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun VerifyEmail(
    email: String,
    onResend: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // Throttle the number of times the user can resend the verification email to satisfy API limits
    var enableResend by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableIntStateOf(Constants.EMAIL_COOLDOWN_SECONDS) }

    val emailStartIndex = 114 // don't change the string and make me recount ;P

    LaunchedEffect(enableResend) {
        if (!enableResend) {
            secondsRemaining = Constants.EMAIL_COOLDOWN_SECONDS

            while (secondsRemaining > 0) {
                delay(1000.milliseconds)
                secondsRemaining--
            }
            enableResend = true
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.email_verify_header),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = boldAnnotatedString(
                text = stringResource(
                    R.string.email_verify_body,
                    email
                ),
                startIndex = emailStartIndex,
                endIndex = emailStartIndex + email.length
            ),
            style = MaterialTheme.typography.bodyLarge
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.email_verify_retry_text),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f, false)
                )
                TextButton(
                    onClick = {
                        onResend()
                        enableResend = false
                    },
                    enabled = enableResend
                ) {
                    Text(
                        text = stringResource(R.string.email_verify_retry_link),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (!enableResend) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = String.format(
                            LocalLocale.current.platformLocale,
                            "%02d:%02d",
                            secondsRemaining / 60, secondsRemaining % 60
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Button(
                onClick = {
                    onLogout()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.logout),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun VerifyEmailPreview() {
    EZRecipesTheme {
        Surface {
            Column {
                VerifyEmail("test@example.com")
            }
        }
    }
}
