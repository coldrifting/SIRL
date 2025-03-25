package com.coldrifting.sirl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> SwipeList(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    rowItemLayout: @Composable RowScope.(T) -> Unit,
    getKey: (T) -> Int,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    spacing: Dp = 0.dp,
    margin: Dp = 0.dp,
    cornerRadius: Dp = 0.dp,
    scroll: Boolean = true
) {
    val list = remember { mutableStateOf(listOf<ListItem<T>>()) }

    key(listItems) {
        list.value = listItems.map { item -> ListItem(key = getKey(item), item = item) }
    }

    val lastSwiped = remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()

    val verticalArrangement = Arrangement.spacedBy(spacing)
    val columnModifier = modifier
        .fillMaxSize()
        .padding(horizontal = margin)
        .clipToBounds()

    @Composable
    fun content(item: ListItem<T>) {
        SwipeRevealItem(
            index = item.key,
            curIndex = lastSwiped,
            leftAction = leftAction,
            rightAction = rightAction,
            cornerRadius = cornerRadius
        ) {
            Row(
                modifier = Modifier
                    .padding(rowPadding)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                rowItemLayout(item.item)
            }
        }
    }

    if (scroll) {
        LazyColumn(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement,
            state = listState,
        ) {
            items(
                items = list.value,
                key = { it.key }
            ) {
                content(it)
            }
        }
    } else {
        Column(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement
        ) {
            list.value.forEach {
                content(it)
            }
        }
    }
}