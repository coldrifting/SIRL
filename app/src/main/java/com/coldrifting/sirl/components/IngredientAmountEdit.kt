package com.coldrifting.sirl.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coldrifting.sirl.data.enums.UnitType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientAmountEdit(
    placeholderAmount: Float,
    placeholderUnitType: UnitType,
    onSuccess: (UnitType, Float) -> Unit,
    onDismiss: () -> Unit
) {
    var unitType by remember { mutableStateOf(placeholderUnitType) }

    var amount by remember { mutableFloatStateOf(placeholderAmount) }
    var amountAsText by remember { mutableStateOf(placeholderAmount.toString().trimEnd{it == '0'}.trimEnd{it == '.'})}

    var dropDownExpanded by remember {mutableStateOf(false)}

    AlertDialog(
        title = "Select Ingredient Quantity",
        onConfirm = {onSuccess.invoke(unitType, amount)},
        confirmButtonEnabled = amount > 0.0f,
        onDismiss = {
            unitType = UnitType.EACHES
            amount = 0.0f
            amountAsText = ""
            dropDownExpanded = false

            onDismiss.invoke()
        }
    ) {
        Column {
            // Dropdown
            ExposedDropdownMenuBox(
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = it },
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    value = unitType.toString(),
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { dropDownExpanded = false }
                ) {
                    UnitType.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.toString()) },
                            onClick = {
                                unitType = it
                                dropDownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 12.dp))

            // Text input
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = {Text("Amount")},
                value = amountAsText,
                onValueChange = {
                    val output = it.replace(Regex("[^0-9.]"), "")
                    if ( output.filter { char -> char == ".".first()}.length <= 1 ) {
                        amountAsText = output
                        amount = amountAsText.trim().toFloatOrNull() ?: 0.0f
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
        }
    }
}