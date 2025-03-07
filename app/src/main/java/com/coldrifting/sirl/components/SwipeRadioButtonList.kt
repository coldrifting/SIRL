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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.coldrifting.sirl.ui.theme.DelColor
import com.coldrifting.sirl.ui.theme.EditColor

data class SwipeData(val action: (Int) -> Unit, val snapBack: Boolean = false)
data class AuxButtonData(val action: (Int) -> Unit, val icon: ImageVector)
data class ListItem(val id: Int, val text: String)

@Composable
fun SwipeRadioButtonList(
    modifier: Modifier = Modifier,
    listItems: List<ListItem>,
    onSelect: ((Int) -> Unit)? = null,
    auxButton: AuxButtonData? = null,
    leftSwipe: SwipeData? = null,
    rightSwipe: SwipeData? = null
) {
    val (selectedOption, onOptionSelected) = remember { mutableIntStateOf(0) }
    val lastSwiped = remember { mutableIntStateOf(-1) }

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    LazyColumn(
        modifier.selectableGroup()
    ) {
        items(listItems.sortedBy { it.text }) { item ->
            val leftAction = if (leftSwipe != null) SwipeTapAction(
                Color.White,
                EditColor,
                Icons.Default.Edit,
                { leftSwipe.action(item.id) },
                leftSwipe.snapBack,
                "Edit"
            ) else null

            val rightAction = if (rightSwipe != null) SwipeTapAction(
                Color.White,
                DelColor,
                Icons.Default.Delete,
                { rightSwipe.action(item.id) },
                rightSwipe.snapBack,
                "Delete"
            ) else null

            SwipeRevealItem(
                index = item.id,
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
                            selected = (item.id == selectedOption),
                            onClick = { onOptionSelected(item.id); onSelect?.invoke(item.id) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item.id == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    if (auxButton != null) {
                        IconButton(onClick = { auxButton.action(item.id) })
                        {
                            Icon(auxButton.icon, "")
                        }
                    }
                }
            }
        }
    }
}