package com.abhiek.ezrecipes.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

@Composable
fun ErrorAlert(
    title: String? = null,
    message: String? = null,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title ?: stringResource(R.string.error_title)
            )
        },
        text = {
            Text(
                text = message ?:
                stringResource(R.string.unknown_error)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.ok_button)
                )
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

private data class ErrorAlertState(
    val title: String? = null,
    val message: String? = null
)

private class ErrorAlertPreviewParameterProvider : PreviewParameterProvider<ErrorAlertState> {
    override val values = sequenceOf(
        ErrorAlertState(
            title = "Sample Title",
            message = "Sample error alert"
        ),
        ErrorAlertState()
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun ErrorAlertPreview(
    @PreviewParameter(ErrorAlertPreviewParameterProvider::class) state: ErrorAlertState
) {
    EZRecipesTheme {
        Surface {
            ErrorAlert(
                title = state.title,
                message = state.message
            )
        }
    }
}
