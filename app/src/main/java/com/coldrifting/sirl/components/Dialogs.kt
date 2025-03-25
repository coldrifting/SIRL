package com.coldrifting.sirl.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
        title = { Text(title) },
        content = {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = textFieldValue,
                placeholder = { Text(placeholder) },
                onValueChange = { textFieldValue = it },
                singleLine = true
            )
        },
        dismissButton = {
            TextButton(
                onClick =
                {
                    onDismiss.invoke()
                },
                content = { Text("Cancel") }
            )
        },
        confirmButton = {
            TextButton(
                enabled = textFieldValue.text.trim()
                    .isNotEmpty() && defaultValue != textFieldValue.text.trim(),
                onClick =
                {
                    onSuccess.invoke(textFieldValue.text.trim())
                    onDismiss.invoke()
                },
                content = { Text(action) }
            )
        },
        onDismiss =
        {
            onDismiss.invoke()
            textFieldValue = TextFieldValue("")
        },
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun AlertDialog(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        // Don't readjust position for soft keyboard
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Box(modifier = Modifier.padding(bottom = 50.dp))
        {
            Surface(shape = MaterialTheme.shapes.medium)
            {
                Column {
                    Column(
                        Modifier.padding(
                            top = 24.dp,
                            bottom = 10.dp,
                            start = 24.dp,
                            end = 24.dp
                        )
                    ) {
                        title.invoke()
                        Spacer(Modifier.size(16.dp))
                        content.invoke()
                    }
                    Spacer(Modifier.size(2.dp))
                    Row(
                        Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        Arrangement.spacedBy(8.dp, Alignment.End),
                    ) {
                        dismissButton.invoke()
                        confirmButton.invoke()
                    }
                }
            }
        }
    }
}