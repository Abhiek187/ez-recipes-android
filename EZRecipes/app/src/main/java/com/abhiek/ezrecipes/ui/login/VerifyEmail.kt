package com.abhiek.ezrecipes.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.boldAnnotatedString

@Composable
fun VerifyEmail(email: String? = null) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.email_verify_header),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = boldAnnotatedString(
                text = stringResource(
                    R.string.email_verify_body,
                    email ?: "your email address"
                ),
                startIndex = 114 // don't change the string and make me recount ;P
            ),
            style = MaterialTheme.typography.bodyLarge
        )
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
                VerifyEmail()
            }
        }
    }
}
