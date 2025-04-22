package com.coldrifting.sirl.components.swipe

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
    onDragStopped: (List<T>, Int) -> Unit,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    scrollTo: Int
) {
    val view = LocalView.current

    val list = remember { mutableStateOf(listOf<ListItem<T>>()) }

    key(listItems) {
        list.value = listItems.map{item -> ListItem(key = getKey(item), item = item) }
    }

    val lastSwiped = remember { mutableIntStateOf(-1) }

    val lazyListState = remember(scrollTo) { LazyListState(firstVisibleItemIndex = scrollTo) }
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
                            modifier = Modifier.padding(horizontal = 16.dp),
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
                                        Log.d("TEST", lazyListState.firstVisibleItemIndex.toString())
                                        onDragStopped(list.value.map{entry -> entry.item}, lazyListState.firstVisibleItemIndex)
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
}