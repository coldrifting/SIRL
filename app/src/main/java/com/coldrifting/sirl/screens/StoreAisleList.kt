package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.AppViewModel
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeReorderableList
import com.coldrifting.sirl.components.TextDialog
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.components.swipeEditAction

@Composable
fun StoreAisleList(id: Int, navHostController: NavHostController, viewModel: AppViewModel, title: String) {
    viewModel.setCurrentStoreForEdit(id)
    val aisles by viewModel.locations.collectAsState()

    var lastTextValue by remember { mutableStateOf("") }
    var listItem by remember { mutableIntStateOf(-1) }

    var showRenameAlertDialog by remember { mutableStateOf(false) }
    if (showRenameAlertDialog) {
        TextDialog(
            title = "Rename Aisle",
            placeholder = "Aisle Name",
            action = "Rename",
            onSuccess = {viewModel.renameAisle(listItem, it)},
            onDismiss = {showRenameAlertDialog = false},
            defaultValue = lastTextValue
        )
    }

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(
            title = "Add Aisle",
            placeholder = "Aisle Name",
            action = "Add",
            onSuccess = {viewModel.addAisle(id, it)},
            onDismiss = {showNewAlertDialog = false}
        )
    }

    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController) },
        floatingActionButton = { Fab(addAction = { showNewAlertDialog = true }) },
        content = { innerPadding ->
            SwipeReorderableList(
                modifier = Modifier.padding(innerPadding),
                listItems = aisles,
                toString = {it.locationName},
                getKey = {it.locationId},
                onDragStopped = { l -> viewModel.syncAisles(l) },
                leftAction = swipeEditAction
                {
                    listItem = it
                    lastTextValue = viewModel.getAisleName(it) ?: ""
                    showRenameAlertDialog = true
                },
                rightAction = swipeDeleteAction
                {
                    viewModel.deleteAisle(it)
                },
            )
        }
    )
}