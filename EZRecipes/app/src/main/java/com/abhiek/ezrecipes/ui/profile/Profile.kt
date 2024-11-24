package com.abhiek.ezrecipes.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.AuthState
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun Profile(profileViewModel: ProfileViewModel, deepLinkAction: String? = null) {
    val authState = profileViewModel.authState
    val chef = profileViewModel.chef

    val context = LocalContext.current

    LaunchedEffect(Unit, deepLinkAction) {
        // Check if the user is authenticated every time the profile tab is launched or deep linked
        profileViewModel.getChef()

        when (deepLinkAction) {
            Constants.ProfileActions.VERIFY_EMAIL -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.email_verify_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
            Constants.ProfileActions.CHANGE_EMAIL -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.change_email_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
            Constants.ProfileActions.RESET_PASSWORD -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.change_password_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    if (authState == AuthState.AUTHENTICATED && chef != null) {
        ProfileLoggedIn(chef, profileViewModel)
    } else if (authState == AuthState.UNAUTHENTICATED) {
        ProfileLoggedOut(profileViewModel)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )
    profileViewModel.authState = state.authState
    profileViewModel.chef = chefService.chef

    EZRecipesTheme {
        Surface {
            Profile(profileViewModel)
        }
    }
}
