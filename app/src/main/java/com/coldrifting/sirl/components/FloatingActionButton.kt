package com.coldrifting.sirl.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import kotlin.random.Random

fun getStoreNameString(): String {
    val list = listOf("Smiths", "Macey's", "Harmon's", "Fresh Market", "WinCo", "Albertson's")
    val num = Random.nextInt(0, list.size - 1)
    return list[num]
}

@Composable
fun Fab(addAction: () -> Unit) {
    FloatingActionButton(
        onClick = addAction
    ) {
        Icon(Icons.Filled.Add, "Add")
    }
}