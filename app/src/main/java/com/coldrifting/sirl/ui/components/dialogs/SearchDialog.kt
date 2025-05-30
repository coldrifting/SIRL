package com.coldrifting.sirl.ui.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchDialog(
    title: String,
    textLabel: String,
    entries: List<T>,
    toString: ((T) -> String)? = null,
    onSuccess: (T) -> Unit,
    onDismiss: () -> Unit
) {
    var dropDownExpanded by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableIntStateOf(-1) }

    var searchText by remember { mutableStateOf("") }

    AlertDialog(
        title = title,
        confirmButtonEnabled = selectedEntry != -1,
        bottomPadding = 200,
        onConfirm = {
            onSuccess.invoke(entries[selectedEntry])

            onDismiss.invoke()
        },
        onDismiss = {
            // Reset dialog state
            selectedEntry = -1
            searchText = ""

            onDismiss.invoke()
        }
    ) {
        Box(modifier = Modifier.wrapContentSize(Alignment.BottomStart)) {
            ExposedDropdownMenuBox(
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = it },
            ) {
                ExposedDropdownMenu(
                    modifier = Modifier.heightIn(max = 200.dp),
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false }
                ) {
                    entries.forEachIndexed { i, item ->
                        if (item.toString().lowercase().contains(searchText.lowercase())
                        ) {
                            DropdownMenuItem(
                                text = { Text(toString?.invoke(item) ?: item.toString()) },
                                onClick = {
                                    selectedEntry = i
                                    searchText = toString?.invoke(item) ?: item.toString()
                                    dropDownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                TextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
                    label = { Text(textLabel) },
                    value = searchText,
                    onValueChange = { searchText = it },
                    trailingIcon = {
                        if (searchText != "")
                            IconButton(onClick = { searchText = ""; selectedEntry = -1 }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                    },
                    readOnly = false,
                    singleLine = true
                )
            }
        }
    }
}