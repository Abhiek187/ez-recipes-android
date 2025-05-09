package com.abhiek.ezrecipes.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abhiek.ezrecipes.R
import com.abhiek.ezrecipes.ui.previews.DevicePreviews
import com.abhiek.ezrecipes.ui.previews.DisplayPreviews
import com.abhiek.ezrecipes.ui.previews.FontPreviews
import com.abhiek.ezrecipes.ui.previews.OrientationPreviews
import com.abhiek.ezrecipes.ui.theme.EZRecipesTheme

// Source: https://stackoverflow.com/a/68995732
@Composable
fun Accordion(
    header: String,
    modifier: Modifier = Modifier,
    expandByDefault: Boolean = true,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(expandByDefault) }

    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    isExpanded = !isExpanded
                    if (isExpanded) onExpand() else onCollapse()
                }
        ) {
            Text(
                text = header,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.run {
                    if (isExpanded) KeyboardArrowUp else KeyboardArrowDown
                },
                contentDescription = if (isExpanded) {
                    stringResource(R.string.accordion_collapse)
                } else {
                    stringResource(R.string.accordion_expand)
                },
                tint = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.6f
                )
            )
        }
        // Animate the content sliding when expanding or collapsing
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            content()
        }
        HorizontalDivider()
    }
}

@DevicePreviews
@DisplayPreviews
@FontPreviews
@OrientationPreviews
@Composable
private fun AccordionPreview() {
    EZRecipesTheme {
        Surface {
            Column {
                Accordion(
                    header = "Collapsed",
                    expandByDefault = false,
                    content = {
                        Text(
                            text = "Collapsed by default",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                )
                Accordion(
                    header = "Expanded",
                    content = {
                        Text(
                            text = "Expanded by default",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                )
            }
        }
    }
}
