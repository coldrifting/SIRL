package com.coldrifting.sirl.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class ListItem<T>(val key: Int, val item: T)

@Composable
fun <T> SwipeReorderableList(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    toString: (T) -> String,
    getKey: (T) -> Int,
    onDragStopped: (List<T>) -> Unit,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
) {
    val view = LocalView.current

    val list = remember { mutableStateOf(listOf<ListItem<T>>()) }

    key(listItems) {
        list.value = listItems.map{item -> ListItem(key = getKey(item), item = item) }
    }

    val lastSwiped = remember { mutableIntStateOf(-1) }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        onMove = { from, to ->
            list.value = list.value.toMutableList().apply{ add(to.index, removeAt(from.index)) }
        }
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize()
    )
    {
        items(
            items = list.value,
            key = { it.key }
        ) {
            ReorderableItem(
                state = reorderableLazyListState,
                key = it.key
            )
            { isDragging ->
                val elevation: Dp by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = elevation,
                    shadowElevation = elevation
                )
                {
                    SwipeRevealItem(
                        index = it.key,
                        curIndex = lastSwiped,
                        leftAction = leftAction,
                        rightAction = rightAction
                    )
                    {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Text(toString(it.item))
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {
                                        view.performHapticFeedback(HapticFeedbackConstantsCompat.LONG_PRESS)
                                    },
                                    onDragStopped = {
                                        view.performHapticFeedback(HapticFeedbackConstantsCompat.GESTURE_END)
                                        onDragStopped(list.value.map{entry -> entry.item})
                                    }),
                                onClick = {})
                            {
                                Icon(Icons.Rounded.Menu, contentDescription = "Reorder")
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    val openAlertDialog = remember { mutableStateOf(false) }
    val listItem = remember { mutableIntStateOf(-1) }

    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Confirmation registered") // Add logic here to handle confirmation.

                    list.removeAt(listItem.intValue)

                },
                dialogTitle = "Alert dialog example",
                dialogText = "This is an example of an alert dialog with buttons.",
                icon = Icons.Default.Info
            )
        }
    }
     */
}