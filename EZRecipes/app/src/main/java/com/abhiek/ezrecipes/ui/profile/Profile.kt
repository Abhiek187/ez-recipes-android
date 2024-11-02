package com.abhiek.ezrecipes.ui.profile

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun Profile(profileViewModel: ProfileViewModel) {
    val authState = profileViewModel.authState
    val chef = profileViewModel.chef

    if (authState == AuthState.AUTHENTICATED && chef != null) {
        ProfileLoggedIn(chef)
    } else if (authState == AuthState.UNAUTHENTICATED) {
        ProfileLoggedOut()
    } else {
        CircularProgressIndicator()
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
    val chefService = MockChefService
    val profileViewModel = ProfileViewModel(ChefRepository(chefService))
    profileViewModel.authState = state.authState
    profileViewModel.chef = MockChefService.chef

    EZRecipesTheme {
        Surface {
            Profile(profileViewModel)
        }
    }
}
