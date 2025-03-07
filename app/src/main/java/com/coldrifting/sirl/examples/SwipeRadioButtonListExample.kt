package com.coldrifting.sirl.examples

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.coldrifting.sirl.components.ListItem
import com.coldrifting.sirl.components.SwipeRadioButtonList
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeRadioButtonListExample() {
    val list = remember { mutableStateListOf(
        ListItem(1, "Macey's (1700 S)"),
        ListItem(2, "WinCo (2100 S)"),
        ListItem(3, "Harmon's")
    ) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* list.add(ListItem(getNextListId(list), getStoreNameString())) */ },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Locations")
                }
            )
        },
        content = { innerPadding ->
            val faves = remember { mutableStateListOf<Int>() }
            val context = LocalContext.current

            SwipeRadioButtonList(
                modifier= Modifier.padding(innerPadding),
                listItems = list)
                //favorites = faves,
                //onEdit = { Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show() })
        })
}