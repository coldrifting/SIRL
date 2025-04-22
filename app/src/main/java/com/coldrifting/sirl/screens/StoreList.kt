package com.coldrifting.sirl.screens

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.routes.RouteStoreAisleList
import com.coldrifting.sirl.components.swipe.AuxButtonData
import com.coldrifting.sirl.components.AppNavBar
import com.coldrifting.sirl.components.swipe.SwipeRadioButtonList
import com.coldrifting.sirl.components.dialogs.TextDialog
import com.coldrifting.sirl.components.AppTopBar
import com.coldrifting.sirl.components.swipe.swipeDeleteAction
import com.coldrifting.sirl.components.swipe.swipeEditAction
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.routes.top.TopLevelRoute.Companion.routeStores
import com.coldrifting.sirl.ui.theme.SIRLTheme

@Composable
fun StoreList(
    navHostController: NavHostController,
    addStore: (String) -> Unit,
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
            onSuccess = { addStore(it) },
            onDismiss = { showNewAlertDialog = false }
        )
    }

    Scaffold(
        topBar = { AppTopBar(navHostController, "Stores") },
        bottomBar = { AppNavBar(navHostController, routeStores) },
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StoreListPreview() {
    SIRLTheme {
        StoreList(
            navHostController = rememberNavController(),
            addStore = { },
            renameStore = { _, _ -> },
            deleteStore = { },
            selectStore = { },
            getStoreName = { "Store Name" },
            selectedStore = 1,
            storeList = listOf(
                Store(0, "Macey's"),
                Store(1, "Harmon's")
            )
        )
    }
}