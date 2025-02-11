package com.coldrifting.sirl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.coldrifting.sirl.examples.SwipeRadioButtonListExample
import com.coldrifting.sirl.examples.SwipeReorderableListExample
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SIRLTheme {
                SwipeRadioButtonListExample()
            }
        }
    }
}

fun getStoreNameString(): String {
    val list = listOf("Smiths", "Maceys", "Harmons", "Fresh Market", "WinCo", "Albersons")

    val num = Random.nextInt(0, list.size - 1)

    return list[num]
}

fun getNextListId(list: List<ListItem>): Int {
    if (list.isNotEmpty()) {
        return (list.maxOfOrNull { it.id } ?: -1) + 1
    }
    return 0
}