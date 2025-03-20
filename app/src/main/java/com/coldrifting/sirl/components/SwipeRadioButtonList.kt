package com.coldrifting.sirl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

data class AuxButtonData(val action: (Int) -> Unit, val icon: ImageVector)

@Composable
fun <T> SwipeRadioButtonList(
    modifier: Modifier = Modifier,
    listItems: List<T>,
    toString: (T) -> String,
    getKey: (T) -> Int,
    selectedItem: Int,
    onSelectItem: (Int) -> Unit,
    auxButton: AuxButtonData? = null,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null
) {
    val lastSwiped = remember { mutableIntStateOf(-1) }

    val list = remember { mutableStateOf(listOf<ListItem<T>>()) }

    key(listItems) {
        list.value = listItems.map{item -> ListItem(getKey(item), item) }
    }

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    LazyColumn(
        modifier.selectableGroup()
    ) {
        items(list.value, key = { it.key }) { item ->
            SwipeRevealItem(
                index = item.key,
                curIndex = lastSwiped,
                leftAction = leftAction,
                rightAction = rightAction
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .selectable(
                            selected = (item.key == selectedItem),
                            onClick = { onSelectItem(item.key) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item.key == selectedItem),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = toString(item.item),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    if (auxButton != null) {
                        IconButton(onClick = { auxButton.action(item.key) })
                        {
                            Icon(auxButton.icon, "")
                        }
                    }
                }
            }
        }
    }
}