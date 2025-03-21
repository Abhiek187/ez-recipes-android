package com.abhiek.ezrecipes.ui.util

import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.abhiek.ezrecipes.data.recipe.MockRecipeService
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

/**
 * Renders an HTML string into a TextView
 *
 * @param html The HTML string to render
 * @param modifier The modifier to apply to this composable
 * @param color The text color value
 * @param fontSize The font size in sp
 */
@Composable
fun HTMLText(
    html: String,
    modifier: Modifier = Modifier,
    color: Int? = null,
    fontSize: Float? = null
) {
    /* AndroidView bridges between Jetpack Compose Views and Android Views
     * factory = the initializer for the Android View
     * update = the logic invoked after the View is rendered
     */
    AndroidView(
        factory = { context -> TextView(context) },
        modifier = modifier,
        update = { view ->
            view.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            // Make anchor tags clickable (autoLink doesn't work if the link text differs from its source)
            view.movementMethod = LinkMovementMethod.getInstance()

            if (color != null) {
                view.setTextColor(color)
            }

            if (fontSize != null) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            }
        }
    )
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
fun HTMLTextPreview() {
    EZRecipesTheme {
        Surface {
            Column {
                HTMLText(MockRecipeService.recipes[1].summary)
                HTMLText(
                    html = MockRecipeService.recipes[1].summary,
                    color = Color.Red.toArgb(),
                    fontSize = 30f
                )
            }
        }
    }
}
