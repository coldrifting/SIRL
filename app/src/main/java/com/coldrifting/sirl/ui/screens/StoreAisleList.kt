package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeItems
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.dialogs.TextDialog
import com.coldrifting.sirl.ui.components.swipe.SwipeReorderableList
import com.coldrifting.sirl.ui.components.swipe.swipeDeleteAction
import com.coldrifting.sirl.ui.components.swipe.swipeEditAction

@Composable
fun StoreAisleList(
    navHostController: NavHostController,
    store: Store,
    addAisle: (Int, String) -> Unit,
    renameAisle: (Int, String) -> Unit,
    deleteAisle: (Int) -> Unit,
    getAisleName: (Int) -> String,
    syncAisles: (Int, List<Aisle>, Int) -> Unit,
    aisles: List<Aisle>,
    scrollTo: Int
) {
    var lastTextValue by remember { mutableStateOf("") }
    var listItem by remember { mutableIntStateOf(-1) }

    var showRenameAlertDialog by remember { mutableStateOf(false) }
    if (showRenameAlertDialog) {
        TextDialog(
            title = "Rename Aisle",
            placeholder = "Aisle Name",
            action = "Rename",
            onSuccess = { renameAisle(listItem, it) },
            onDismiss = { showRenameAlertDialog = false },
            defaultValue = lastTextValue
        )
    }

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(
            title = "Add Aisle",
            placeholder = "Aisle Name",
            action = "Add",
            onSuccess = { addAisle(store.storeId, it) },
            onDismiss = { showNewAlertDialog = false }
        )
    }

    Scaffold(
        topBar = { AppTopBar(navHostController, "${store.storeName} - Aisles") },
        bottomBar = { AppNavBar(navHostController, routeItems) },
        floatingActionButton = {
            FloatingActionButton(onClick = {showNewAlertDialog = true}) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        content = { innerPadding ->
            SwipeReorderableList(
                modifier = Modifier.padding(innerPadding),
                listItems = aisles,
                toString = { it.aisleName },
                getKey = { it.aisleId },
                onDragStopped = { l, i -> syncAisles(store.storeId, l, i) },
                leftAction = swipeEditAction
                {
                    listItem = it
                    lastTextValue = getAisleName(it)
                    showRenameAlertDialog = true
                },
                rightAction = swipeDeleteAction
                {
                    deleteAisle(it)
                },
                scrollTo = scrollTo
            )
        }
    )
}