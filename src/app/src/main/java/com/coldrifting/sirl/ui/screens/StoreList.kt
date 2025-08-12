package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
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
import com.coldrifting.sirl.data.RouteStoreAisleList
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeItems
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.dialogs.TextDialog
import com.coldrifting.sirl.ui.components.swipe.AuxButtonData
import com.coldrifting.sirl.ui.components.swipe.SwipeRadioButtonList
import com.coldrifting.sirl.ui.components.swipe.swipeDeleteAction
import com.coldrifting.sirl.ui.components.swipe.swipeEditAction

@Composable
fun StoreList(
    navHostController: NavHostController,
    addStore: (String, Boolean) -> Unit,
    renameStore: (Int, String) -> Unit,
    deleteStore: (Int) -> Unit,
    selectStore: (Int) -> Unit,
    getStoreName: (Int) -> String,
    selectedStore: Int,
    storeList: List<Store>
) {
    var lastTextValue by remember { mutableStateOf("") }
    var listItem by remember { mutableIntStateOf(-1) }

    var showRenameAlertDialog by remember { mutableStateOf(false) }
    if (showRenameAlertDialog) {
        TextDialog(
            title = "Rename Store",
            placeholder = "Store Name",
            action = "Rename",
            onSuccess = { renameStore(listItem, it) },
            onDismiss = { showRenameAlertDialog = false },
            defaultValue = lastTextValue
        )
    }

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(
            title = "Add Store",
            placeholder = "Store Name",
            action = "Add",
            onSuccess = { addStore(it, storeList.isEmpty()) },
            onDismiss = { showNewAlertDialog = false }
        )
    }

    Scaffold(
        topBar = { AppTopBar(navHostController, "Stores") },
        bottomBar = { AppNavBar(navHostController, routeItems) },
        floatingActionButton = {
            FloatingActionButton(onClick = {showNewAlertDialog = true}) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        content = { innerPadding ->
            SwipeRadioButtonList(
                modifier = Modifier.padding(innerPadding),
                listItems = storeList,
                toString = { it.storeName },
                getKey = { it.storeId },
                auxButton = AuxButtonData(
                    { navHostController.navigate(RouteStoreAisleList(it)) },
                    Icons.Filled.PlayArrow
                ),
                selectedItem = selectedStore,
                onSelectItem = selectStore,
                leftAction = swipeEditAction
                {
                    listItem = it
                    lastTextValue = getStoreName(it)
                    showRenameAlertDialog = true
                },
                rightAction = swipeDeleteAction
                {
                    deleteStore(it)
                }
            )
        }
    )
}