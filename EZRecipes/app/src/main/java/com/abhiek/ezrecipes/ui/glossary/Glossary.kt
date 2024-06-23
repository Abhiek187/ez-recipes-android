package com.abhiek.ezrecipes.ui.glossary

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.data.models.Term
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme
import com.abhiek.ezrecipes.utils.Constants
import com.abhiek.ezrecipes.utils.boldAnnotatedString

@Composable
fun Glossary(terms: List<Term>) {
    LazyColumn(
        modifier = Modifier.padding(8.dp)
    ) {
        // Sort all the terms alphabetically for ease of reference
        itemsIndexed(terms.sortedBy { it.word }) { index, term ->
            Text(
                text = boldAnnotatedString(
                    text = "${term.word} — ${term.definition}",
                    endIndex = term.word.length
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (index < terms.lastIndex) {
                Divider()
            }
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
