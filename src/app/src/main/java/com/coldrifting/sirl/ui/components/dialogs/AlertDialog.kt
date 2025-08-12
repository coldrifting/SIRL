package com.coldrifting.sirl.ui.components.dialogs

import android.view.Gravity
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun AlertDialog(
    title: String,
    dismissText: String = "Cancel",
    confirmText: String = "Confirm",
    confirmButtonEnabled: Boolean = true,
    showConfirmButton: Boolean = true,
    showDismissButton: Boolean = true,
    bottomPadding: Int = 0,
    onConfirm: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        // Don't re-adjust position for soft keyboard
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        (LocalView.current.parent as DialogWindowProvider).apply {
            window.setGravity(Gravity.CENTER)
            window.attributes = window.attributes.apply {
                y = -bottomPadding
            }
        }

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
                    Text(title)
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
                    if (showDismissButton) {
                        TextButton(
                            onClick = {
                                onDismiss.invoke()
                            },
                            content = { Text(dismissText, color = MaterialTheme.colorScheme.outline) }
                        )
                    }
                    if (showConfirmButton) {
                        TextButton(
                            enabled = confirmButtonEnabled,
                            onClick = {
                                onConfirm?.invoke()
                                onDismiss.invoke()
                            },
                            content = { Text(confirmText) }
                        )
                    }
                }
            }
        }
    }
}