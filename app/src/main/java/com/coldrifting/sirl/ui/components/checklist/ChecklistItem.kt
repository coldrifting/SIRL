package com.coldrifting.sirl.ui.components.checklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChecklistItem(
    name: String,
    info: String? = null,
    details: String? = null,
    indentLevel: Int = 2,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(tonalElevation = 1.dp, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier
                .clickable { onClick.invoke() }
                .padding(vertical = 12.dp)
                .padding(start = 10.dp * indentLevel, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(name)
            if (info != null) {
                Spacer(Modifier.width(10.dp))
                Text(
                    text = info,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.weight(1f))
            if (details != null) {
                Text(
                    text = details,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(16.dp))
            }
            Checkbox(
                modifier = Modifier.padding(0.dp),
                checked = checked,
                onCheckedChange = null
            )
        }
    }
}