package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import com.coldrifting.sirl.StoreAisleList
import com.coldrifting.sirl.components.AuxButtonData
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeRadioButtonList
import com.coldrifting.sirl.components.TextDialog
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.components.swipeEditAction

@Composable
fun StoreList(navHostController: NavHostController, viewModel: AppViewModel, title: String) {
    val storeList by viewModel.stores.collectAsState()
    val selectedStore by viewModel.selectedStore.collectAsState()

    var lastTextValue by remember { mutableStateOf("") }
    var listItem by remember { mutableIntStateOf(-1) }

    var showRenameAlertDialog by remember { mutableStateOf(false) }
    if (showRenameAlertDialog) {
        TextDialog(
            title = "Rename Store",
            placeholder = "Store Name",
            action = "Rename",
            onSuccess = {viewModel.renameStore(listItem, it)},
            onDismiss = {showRenameAlertDialog = false},
            defaultValue = lastTextValue
        )
    }

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(
            title = "Add Store",
            placeholder = "Store Name",
            action = "Add",
            onSuccess = {viewModel.addStore(it)},
            onDismiss = {showNewAlertDialog = false}
        )
    }

    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController) },
        floatingActionButton = { Fab(addAction = { showNewAlertDialog = true }) },
        content = { innerPadding ->
            SwipeRadioButtonList(
                modifier = Modifier.padding(innerPadding),
                listItems = storeList,
                toString = { it.storeName },
                getKey = {it.storeId },
                auxButton = AuxButtonData(
                    { navHostController.navigate(StoreAisleList(it)) },
                    Icons.Filled.PlayArrow
                ),
                selectedItem = selectedStore,
                onSelectItem = viewModel::selectStore,
                leftAction = swipeEditAction
                {
                    listItem = it
                    lastTextValue = viewModel.getStoreName(it) ?: ""
                    showRenameAlertDialog = true
                },
                rightAction = swipeDeleteAction
                {
                    viewModel.deleteStore(it)
                }
            )
        }
    )
}