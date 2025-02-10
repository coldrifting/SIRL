package com.coldrifting.sirl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

data class ListItem(val id:Int, val text:String)

@Composable
fun SwipeRadioButtonList(modifier: Modifier = Modifier,
                               list: MutableList<ListItem>,
                               onEdit: (Int) -> Unit,
                               onDelete: (Int) -> Unit) {
    val (selectedOption, onOptionSelected) = remember { mutableIntStateOf(0) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    LazyColumn(modifier.selectableGroup()) {
        items(count = list.size , key = { index -> list[index].id}) { index ->
            SwipeRevealItem(onRightAction = {
                onDelete.invoke(index)
            }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .selectable(
                            selected = (index == selectedOption),
                            onClick = { onOptionSelected(index) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = list[index].text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {onEdit.invoke(index)}) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }
            }

        }
    }
}