package com.coldrifting.sirl.components

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun SwipeReorderableList(
    modifier: Modifier = Modifier,
    listItems: List<String>,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    onMove: (from: Int, to: Int) -> Unit,
    onDragStopped: () -> Unit
) {
    val view = LocalView.current

    val lastSwiped = remember { mutableIntStateOf(-1) }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        onMove = { from, to ->
            onMove( from.index, to.index)
        }
    )

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
    )
    {
            items(listItems, key = { it }) {
                Log.d("TEST", "CurrentIndexList: $it")
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = it
                ) { isDragging ->
                    val elevation: Dp by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    //val color: Color = MaterialTheme.colorScheme.surfaceContainer
                    val color: Color by animateColorAsState(if (isDragging) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer)

                    Surface(color = color, shadowElevation = elevation) {
                        SwipeRevealItem(
                            index = it.hashCode(),
                            curIndex = lastSwiped,
                            leftAction = leftAction,
                            rightAction = rightAction
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = it)
                                Spacer(Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            view.performHapticFeedback(HapticFeedbackConstantsCompat.LONG_PRESS)
                                        },
                                        onDragStopped = {
                                            view.performHapticFeedback(HapticFeedbackConstantsCompat.GESTURE_END)
                                            onDragStopped()
                                        },
                                    ),
                                        onClick = {},
                                ) {
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