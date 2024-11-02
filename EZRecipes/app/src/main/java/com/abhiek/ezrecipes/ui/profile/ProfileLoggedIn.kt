package com.abhiek.ezrecipes.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.data.chef.MockChefService
import com.abhiek.ezrecipes.data.models.Chef
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun ProfileLoggedIn(chef: Chef) {
    Column {
        Text(
            text = stringResource(R.string.profile_header, chef.email),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.profile_favorites),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Row {
            for (id in chef.favoriteRecipes) {
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
            for ((id, timestamp) in chef.recentRecipes.entries) {
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
            for ((id, rating) in chef.ratings.entries) {
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
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ProfileLoggedInPreview() {
    EZRecipesTheme {
        Surface {
            ProfileLoggedIn(MockChefService.chef)
        }
    }
}
