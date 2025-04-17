package com.coldrifting.sirl.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Section(
    title: String,
    indentLevel: Int = 1,
    collapsable: Boolean = false,
    isSubHeading: Boolean = false,
    startExpanded: Boolean = true,
    subContent: List<Pair<String, @Composable (modifier: Modifier) -> Unit>> = listOf(),
    content: @Composable (modifier: Modifier) -> Unit
) {
    var showContents by remember { mutableStateOf(startExpanded) }

    val padOffset = 16.dp
    val padHeight = if (!isSubHeading) 16.dp else 12.dp

    var headerModifier = if (collapsable) Modifier.clickable { showContents = !showContents } else Modifier

    Column {
        Column(
            modifier = headerModifier
                .padding(top = padHeight, start = padOffset * indentLevel, end = padOffset)
                .fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(bottom = padHeight)
            ) {
                Text(
                    text = title,
                    fontSize = if (isSubHeading) 16.sp else 18.sp
                )

                Spacer(Modifier.weight(1f))

                if (collapsable) {
                    Icon(
                        imageVector = if (showContents) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = "ArrowDown"
                    )
                }
            }
        }

        if (showContents) {
            content(Modifier)

            subContent.forEach { pair ->
                Section(
                    title = pair.first,
                    isSubHeading = true,
                    collapsable = collapsable,
                    indentLevel = indentLevel + 1
                ) {
                    pair.second(
                        Modifier.padding(
                            start = padOffset * (indentLevel + 2),
                            end = padOffset
                        )
                    )
                }
            }
        }
    }
}