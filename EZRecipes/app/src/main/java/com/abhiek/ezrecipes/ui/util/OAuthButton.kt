package com.abhiek.ezrecipes.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Provider
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.profile.ProfileViewModel
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants

// Inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/auth/src/main/java/com/firebase/ui/auth/ui/components/AuthProviderButton.kt
@Composable
fun OAuthButton(
    provider: Provider,
    authUrl: String? = null,
    profileViewModel: ProfileViewModel
) {
    Button(
        onClick = {
            println("Opening $authUrl")
        },
        enabled = authUrl != null,
        colors = ButtonDefaults.buttonColors(
            containerColor = provider.style.backgroundColor,
            contentColor = provider.style.contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(provider.style.icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = provider.style.label,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun OAuthButtonPreview() {
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

    EZRecipesTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                OAuthButton(
                    provider = Provider.GOOGLE,
                    authUrl = Constants.Mocks.AUTH_URLS[0].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.FACEBOOK,
                    authUrl = Constants.Mocks.AUTH_URLS[1].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.GITHUB,
                    authUrl = Constants.Mocks.AUTH_URLS[2].authUrl,
                    profileViewModel = profileViewModel
                )
                OAuthButton(
                    provider = Provider.GOOGLE,
                    profileViewModel = profileViewModel
                ) // disabled
            }
        }
    }
}
