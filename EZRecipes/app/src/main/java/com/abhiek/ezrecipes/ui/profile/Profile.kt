package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun Profile(profileViewModel: ProfileViewModel) {
    val authState = profileViewModel.authState
    val profile = profileViewModel.profile

    Column {
        if (authState == AuthState.AUTHENTICATED && profile != null) {
            Text(
                text = stringResource(R.string.profile_header, profile.email),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.profile_favorites),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row {
                for (id in profile.favoriteRecipes) {
                    Text(id)
                }
            }
            HorizontalDivider()

            Text(
                text = stringResource(R.string.profile_recently_viewed),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row {
                for ((id, timestamp) in profile.recentRecipes.entries) {
                    Text("$id: Recently viewed at $timestamp")
                }
            }
            HorizontalDivider()

            Text(
                text = stringResource(R.string.profile_ratings),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Row {
                for ((id, rating) in profile.ratings.entries) {
                    Text("$id: $rating star(s)")
                }
            }

            Button(
                onClick = { println("Logout") }
            ) {
                Text(text = stringResource(R.string.logout))
            }
            Button(
                onClick = { println("Change Email") }
            ) {
                Text(text = stringResource(R.string.change_email))
            }
            Button(
                onClick = { println("Change Password") }
            ) {
                Text(text = stringResource(R.string.change_password))
            }
            Button(
                onClick = { println("Delete Account") }
            ) {
                Text(text = stringResource(R.string.delete_account))
            }
        } else if (authState == AuthState.UNAUTHENTICATED) {
            Text(
                text = stringResource(R.string.login_message)
            )

            Button(
                onClick = { println("Login") }
            ) {
                Text(text = stringResource(R.string.login))
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

private data class ProfileState(
    val authState: AuthState
)

private class ProfilePreviewParameterProvider : PreviewParameterProvider<ProfileState> {
    override val values = sequenceOf(
        ProfileState(AuthState.AUTHENTICATED),
        ProfileState(AuthState.UNAUTHENTICATED),
        ProfileState(AuthState.LOADING)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfilePreview(
    @PreviewParameter(ProfilePreviewParameterProvider::class) state: ProfileState
) {
    val profileViewModel = ProfileViewModel()
    profileViewModel.authState = state.authState

    EZRecipesTheme {
        Surface {
            Profile(profileViewModel)
        }
    }
}
