package com.coldrifting.sirl.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.coldrifting.sirl.AppViewModel
import com.coldrifting.sirl.components.AuxButtonData
import com.coldrifting.sirl.components.ListItem
import com.coldrifting.sirl.StoreAisleList
import com.coldrifting.sirl.components.SwipeData
import com.coldrifting.sirl.components.SwipeRadioButtonList

@Composable
fun StoreList(navHostController: NavHostController, viewModel: AppViewModel) {
    val storeList by viewModel.stores.collectAsState()
    SwipeRadioButtonList(
        listItems = storeList.map { ListItem(it.key, it.value) },
        auxButton = AuxButtonData({navHostController.navigate(StoreAisleList(it))}, Icons.Filled.PlayArrow),
        onSelect = viewModel::selectStore,
        leftSwipe = SwipeData({viewModel.renameStore(it, "blah")}),
        rightSwipe = SwipeData({viewModel.deleteStore(it)})
    )
}