package com.coldrifting.sirl.ui.components.dialogs

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextDialog(
    title: String,
    placeholder: String,
    action: String,
    onSuccess: (String) -> Unit,
    onDismiss: () -> Unit,
    defaultValue: String = ""
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                defaultValue,
                TextRange(defaultValue.length)
            )
        )
    }

    AlertDialog(
        title = title,
        confirmText = action,
        confirmButtonEnabled = textFieldValue.text.trim().isNotEmpty() &&
                defaultValue != textFieldValue.text.trim(),
        onConfirm = {onSuccess.invoke(textFieldValue.text.trim())},
        onDismiss = {
            onDismiss.invoke()
            textFieldValue = TextFieldValue("")
        },
        content = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = textFieldValue,
                placeholder = { Text(placeholder) },
                onValueChange = { textFieldValue = it },
                singleLine = true
            )
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}