package com.coldrifting.sirl.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun <T> TextFieldWithDebounce(
    obj: T,
    label: String,
    getId: (T) -> Int,
    getName: (T) -> String,
    setName: (Int, String) -> Unit
) {
    var text by remember(obj) {
        val x = getName(obj)
        if (x.isNotEmpty())
            mutableStateOf(x)
        else
            mutableStateOf(" ")
    }

    TextField(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
        value = text,
        singleLine = true,
        onValueChange = { text = it },
        label = { Text(label) }
    )

    // Debounce name changes to item
    LaunchedEffect(key1 = text) {
        if (text.trim() == getName(obj))
            return@LaunchedEffect

        delay(500)

        setName(getId(obj), text)
    }
}