package com.coldrifting.sirl.examples

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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.coldrifting.sirl.ListItem
import com.coldrifting.sirl.SwipeRevealItem
import com.coldrifting.sirl.SwipeTapAction
import com.coldrifting.sirl.getNextListId
import com.coldrifting.sirl.getStoreNameString
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeReorderableListExample() {
    val list =
        remember {
            mutableStateListOf(
                ListItem(1, "Maceys (1700 S)"),
                ListItem(2, "WinCo (2100 S)"),
                ListItem(3, "Harmons")
            )
        }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { list.add(ListItem(getNextListId(list), getStoreNameString())) },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Locations")
                }
            )
        },
    ) { innerPadding ->
        val view = LocalView.current

        val lastSwiped = remember { mutableIntStateOf(-1) }

        val lazyListState: LazyListState = rememberLazyListState()
        val reorderableLazyListState: ReorderableLazyListState =
            rememberReorderableLazyListState(lazyListState) { from, to ->
                list.add(to.index, list.removeAt(from.index))

                ViewCompat.performHapticFeedback(
                    view,
                    HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
                )
            }

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            state = lazyListState
        )
        {
            items(count = list.size, key = { index -> list[index].id }) { index ->
                Log.d("", "CurrentIndexList: $index")
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = list[index].id
                ) { isDragging ->
                    val elevation: Dp by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    val color: Color by animateColorAsState(if (index % 2 == 0 || isDragging) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer)

                    val addAction = SwipeTapAction(
                        Color.White,
                        com.coldrifting.sirl.ui.theme.EditColor,
                        Icons.Default.Edit,
                        {},
                        true,
                        "Edit"
                    )
                    val delAction = SwipeTapAction(
                        Color.White,
                        com.coldrifting.sirl.ui.theme.DelColor,
                        Icons.Default.Delete,
                        { list.removeAt(index) },
                        false,
                        "Delete"
                    )

                    Surface(color = color, shadowElevation = elevation) {
                        SwipeRevealItem(
                            index = index,
                            curIndex = lastSwiped,
                            leftAction = addAction,
                            rightAction = delAction
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = list[index].text)
                                Spacer(Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_START
                                            )
                                        },
                                        onDragStopped = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_END
                                            )
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