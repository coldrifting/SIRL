package com.coldrifting.sirl.ui.components.swipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> SwipeList(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    getKey: (T) -> Int,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    rightActionMap: Map<String, SwipeTapAction>? = null,
    tapAction: ((Int) -> Unit)? = null,
    rowPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    spacing: Dp = 0.dp,
    margin: Dp = 0.dp,
    cornerRadius: Dp = 0.dp,
    scroll: Boolean = true,
    top: @Composable (() -> Unit)? = null,
    bottom: @Composable (() -> Unit)? = null,
    rowItemLayout: @Composable RowScope.(T) -> Unit,
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

    @Composable
    fun content(item: ListItem<T>) {
        val className = item.item!!::class.simpleName
        val leftActionAct = leftAction
        val rightActionAct = rightActionMap?.get(className) ?: rightAction

        if (item.item !is String || (leftActionAct != null || rightActionAct != null)) {
            SwipeRevealItem(
                index = item.key,
                curIndex = lastSwiped,
                leftAction = leftActionAct,
                rightAction = rightActionAct,
                cornerRadius = cornerRadius
            ) {
                Row(
                    modifier = Modifier
                        .then(if (tapAction != null) Modifier.clickable { tapAction.invoke(item.key) } else Modifier)
                        .padding(rowPadding)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    rowItemLayout(item.item)
                }
            }
        }
        else {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 3.dp,
                modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(cornerRadius))
            ) {
                Row(
                    modifier = Modifier
                        .then(if (tapAction != null) Modifier.clickable { tapAction.invoke(item.key) } else Modifier)
                        .padding(rowPadding)
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    rowItemLayout(item.item)
                }
            }
        }
    }

    if (scroll) {
        LazyColumn(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement,
            state = listState,
        ) {
            item {Box(modifier = Modifier.padding(vertical = margin / 2)) {}}
            if (top != null) {
                item { top.invoke() }
                item { Box(modifier = Modifier.padding(vertical = margin / 2)) {} }
            }
            items(
                items = list.value,
                key = { it.key }
            ) {
                content(it)
            }
            if (bottom != null) {
                item { Box(modifier = Modifier.padding(vertical = margin / 2)) {} }
                item{ bottom.invoke() }
            }
            item { Box(modifier = Modifier.padding(vertical = margin / 2)) {} }
        }
    } else {
        Column(
            modifier = columnModifier,
            verticalArrangement = verticalArrangement
        ) {
            Box(modifier = Modifier.padding(vertical = margin / 2)) {}
            list.value.forEach {
                content(it)
            }
            Box(modifier = Modifier.padding(vertical = margin / 2)) {}
        }
    }
}