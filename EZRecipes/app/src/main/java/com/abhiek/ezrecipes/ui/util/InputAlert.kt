package com.abhiek.ezrecipes.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun InputAlert(
    inputLabel: String,
    initialInput: String,
    onConfirm: (input: String) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf(initialInput) }

    val inputEmpty = input.isEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            TextField(
                value = input,
                onValueChange = {
                    input = it
                },
                label = {
                    Text(text = inputLabel)
                },
                isError = inputEmpty
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(input)
                    onDismiss()
                },
                enabled = !inputEmpty
            ) {
                Text(
                    text = stringResource(R.string.ok_button)
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.cancel_button)
                )
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun InputAlertPreview() {
    EZRecipesTheme {
        Surface {
            InputAlert(
                inputLabel = "Name",
                initialInput = "Atul Shah",
                onConfirm = {},
                onDismiss = {}
            )
        }
    }
}
