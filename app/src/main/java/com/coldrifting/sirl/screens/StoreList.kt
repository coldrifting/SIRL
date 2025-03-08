package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.AppViewModel
import com.coldrifting.sirl.StoreAisleList
import com.coldrifting.sirl.components.AuxButtonData
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeData
import com.coldrifting.sirl.components.SwipeRadioButtonList
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.getStoreNameString

@Composable
fun StoreList(navHostController: NavHostController, viewModel: AppViewModel, title: String) {
    val storeList by viewModel.stores.collectAsState()
    val selectedStore by viewModel.selectedStore.collectAsState()
    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController) },
        floatingActionButton = { Fab(addAction = {viewModel.addStore(getStoreNameString()) })},
        content = { innerPadding ->
            SwipeRadioButtonList(
                modifier = Modifier.padding(innerPadding),
                listItems = storeList,
                toString = { it.name },
                getKey = {it.storeId },
                auxButton = AuxButtonData(
                    { navHostController.navigate(StoreAisleList(it)) },
                    Icons.Filled.PlayArrow
                ),
                selectedItem = selectedStore,
                onSelectItem = viewModel::selectStore,
                leftSwipe = SwipeData({ viewModel.renameStore(it, "blah") }),
                rightSwipe = SwipeData({ viewModel.deleteStore(it) })
            )
        }
    )
}