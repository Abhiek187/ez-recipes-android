package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun ProfileLoggedOut() {
    Column {
        Text(
            text = stringResource(R.string.login_message)
        )

        Button(
            onClick = { println("Login") }
        ) {
            Text(text = stringResource(R.string.login))
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfileLoggedOutPreview() {
    EZRecipesTheme {
        Surface {
            ProfileLoggedOut()
        }
    }
}
