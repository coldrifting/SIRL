package com.coldrifting.sirl.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> SwipeList(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    rowItemLayout: @Composable RowScope.(T) -> Unit,
    getKey: (T) -> Int,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    val list = remember { mutableStateOf(listOf<ListItem<T>>()) }

    key(listItems) {
        list.value = listItems.map { item -> ListItem(key = getKey(item), item = item) }
    }

    val lastSwiped = remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    )
    {
        items(
            items = list.value,
            key = { it.key }
        ) {
            SwipeRevealItem(
                index = it.key,
                curIndex = lastSwiped,
                leftAction = leftAction,
                rightAction = rightAction
            ) {
                Row(
                    modifier = Modifier.padding(rowPadding).height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    rowItemLayout(it.item)
                }
            }
        }
    }
}