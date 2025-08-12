package com.coldrifting.sirl.ui.components.checklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coldrifting.sirl.ui.components.HtmlView
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.data.objects.ChecklistHeader.Companion.toggleHeader
import com.coldrifting.sirl.data.objects.ChecklistHeader.Companion.toggleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipeDetailsChecklist(
    modifier: Modifier = Modifier,
    entries: List<ChecklistHeader>,
    steps: String
) {
    var coroutineScope = rememberCoroutineScope()

    var entriesState by rememberSaveable(entries) { mutableStateOf(entries) }

    var singleSection = entriesState.count { e -> e.items.isNotEmpty() } == 1

    var collapseAll by remember { mutableStateOf(false) }
    var collapseIndex by remember { mutableStateOf<Int?>(null) }

    var showAllIngredients by remember { mutableStateOf(true) }
    var showSteps by remember { mutableStateOf(true) }

    collapseIndex?.let {
        val tempState = entriesState.toggleHeader(it)
        if (tempState.none{e -> e.expanded}) {
            collapseAll = true
        }
        entriesState = tempState
        collapseIndex = null
    }

    if (collapseAll) {

        val tempState = entriesState
        tempState.forEachIndexed { i, _ ->
            tempState.toggleHeader(i, false)
        }
        entriesState = tempState
        showAllIngredients = false
        collapseAll = false
    }

    Surface(tonalElevation = 1.dp, shadowElevation = 1.dp) {
        LazyColumn(
            modifier = modifier
        ) {
            if (!singleSection) {
                item(key = -1) {
                    CollapsableHeader(
                        title = "Ingredients",
                        isMainHeader = true,
                        expanded = showAllIngredients,
                        onClick = { showAllIngredients = !showAllIngredients }
                    )
                }
            }
            entriesState.filter { e -> e.items.isNotEmpty() }.forEachIndexed { headerIndex, header ->
                item(key = header.id * 100000) {
                    AnimatedVisibility(
                        visible = if (!singleSection) showAllIngredients else true,
                        enter = slideInVertically() + expandVertically(),
                        exit = slideOutVertically() + shrinkVertically()
                    ) {
                        CollapsableHeader(
                            title = if (singleSection) "Ingredients" else header.name,
                            indentLevel = if (singleSection) 1 else 2,
                            isMainHeader = singleSection,
                            expanded = header.expanded,
                            onClick = {
                                if (singleSection) {
                                    showAllIngredients = true
                                }
                                entriesState = entriesState.toggleHeader(headerIndex)
                            }
                        )
                    }
                }
                header.items.forEachIndexed { itemIndex, item ->
                    item(key = (header.id * 100000) + item.id + 1) {
                        AnimatedVisibility(
                            visible = header.expanded && showAllIngredients,
                            enter = slideInVertically() + expandVertically(),
                            exit = slideOutVertically() + shrinkVertically()
                        ) {
                            ChecklistItem(
                                name = item.name,
                                details = item.details,
                                info = item.info,
                                indentLevel = if (singleSection) 2 else 3,
                                checked = item.checked,
                                onClick = {
                                    var newState = entriesState.toggleItem(headerIndex, itemIndex)

                                    if (newState[headerIndex].items.all{e -> e.checked}) {
                                        coroutineScope.launch {
                                            delay(200)
                                            collapseIndex = headerIndex
                                        }
                                    }
                                    entriesState = newState
                                }
                            )
                        }
                    }
                }
            }
            item(key = -2) {
                CollapsableHeader(
                    title = "Steps",
                    isMainHeader = true,
                    expanded = showSteps,
                    onClick = { showSteps = !showSteps }
                )
            }
            item(key = -3) {
                AnimatedVisibility(
                    visible = showSteps,
                    enter = slideInVertically() + expandVertically(),
                    exit = slideOutVertically() + shrinkVertically()
                ) {
                    Surface(
                        tonalElevation = 1.dp, shadowElevation = 1.dp) {
                        HtmlView(steps)
                    }
                }
            }
        }
    }
}