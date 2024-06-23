package com.abhiek.ezrecipes.ui.glossary

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants

@Composable
fun Glossary(terms: List<Term>) {
    LazyColumn {
        items(terms) { term ->
            Text(text ="**${term.word}** — ${term.definition}")
        }
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun GlossaryPreview() {
    EZRecipesTheme {
        Surface {
            Glossary(Constants.Mocks.TERMS)
        }
    }
}
