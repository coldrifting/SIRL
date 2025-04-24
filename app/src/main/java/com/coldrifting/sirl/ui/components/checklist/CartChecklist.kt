package com.coldrifting.sirl.ui.components.checklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coldrifting.sirl.data.objects.ChecklistHeader

@Composable
fun CartChecklist(
    modifier: Modifier = Modifier,
    entries: List<ChecklistHeader>,
    onHeaderClicked: (Int) -> Unit,
    onItemClicked: (Int, Int) -> Unit
) {
    Surface(tonalElevation = 1.dp, shadowElevation = 1.dp) {
        LazyColumn(
            modifier = modifier
        ) {
            entries.forEachIndexed { headerIndex, header ->
                item(key = header.id + 100000) {
                    CollapsableHeader(
                        title = header.name,
                        expanded = header.expanded,
                        onClick = { onHeaderClicked.invoke(headerIndex) }
                    )
                }
                header.items.forEachIndexed { itemIndex, item ->
                    item(key = item.id) {
                        AnimatedVisibility(
                            visible = header.expanded,
                            enter = slideInVertically() + expandVertically(),
                            exit = slideOutVertically() + shrinkVertically()
                        ) {
                            ChecklistItem(
                                name = item.name,
                                details = item.details,
                                checked = item.checked,
                                onClick = { onItemClicked.invoke(headerIndex, itemIndex) }
                            )
                        }
                    }
                }
            }
        }
    }
}