package com.abhiek.ezrecipes.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.ChefRepository
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.data.recipe.RecipeRepository
import com.abhiek.ezrecipes.data.storage.DataStoreService
import com.abhiek.ezrecipes.ui.login.LoginDialog
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun ProfileLoggedOut(profileViewModel: ProfileViewModel) {
    val context = LocalContext.current

    LaunchedEffect(profileViewModel.passwordUpdated, profileViewModel.accountDeleted) {
        // Show toast messages after actions that force the user to be signed out
        if (profileViewModel.passwordUpdated) {
            Toast.makeText(context, R.string.change_password_success, Toast.LENGTH_SHORT).show()
            profileViewModel.passwordUpdated = false
        } else if (profileViewModel.accountDeleted) {
            Toast.makeText(context, R.string.delete_account_success, Toast.LENGTH_SHORT).show()
            profileViewModel.accountDeleted = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.login_message),
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                profileViewModel.openLoginDialog = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.login))
        }

        if (profileViewModel.openLoginDialog) {
            LoginDialog(profileViewModel) {
                profileViewModel.openLoginDialog = false
            }
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfileLoggedOutPreview() {
    val context = LocalContext.current

    val chefService = MockChefService
    val recipeService = MockRecipeService
    val profileViewModel = ProfileViewModel(
        chefRepository = ChefRepository(chefService),
        recipeRepository = RecipeRepository(recipeService),
        dataStoreService = DataStoreService(context)
    )

    EZRecipesTheme {
        Surface {
            ProfileLoggedOut(profileViewModel)
        }
    }
}
