package com.coldrifting.sirl.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.coldrifting.sirl.AppViewModel
import com.coldrifting.sirl.components.Fab
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeReorderableList
import com.coldrifting.sirl.components.SwipeTapAction
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.getStoreNameString
import com.coldrifting.sirl.ui.theme.DelColor
import com.coldrifting.sirl.ui.theme.EditColor

@Composable
fun StoreAisleList(id: Int, navHostController: NavHostController, viewModel: AppViewModel, title: String) {

    val editAction = SwipeTapAction(
        Color.White,
        EditColor,
        Icons.Default.Edit,
        {viewModel.renameAisle(id, it, "blah")},
        true,
        "Edit"
    )
    val delAction = SwipeTapAction(
        Color.White,
        DelColor,
        Icons.Default.Delete,
        {viewModel.deleteAisle(id, it)},
        false,
        "Delete"
    )

    viewModel.setCurrentStoreForEdit(id)
    val aisles by viewModel.aisles.collectAsState()

    Scaffold(
        topBar = { TopBar(navHostController, title) },
        bottomBar = { NavBar(navHostController) },
        floatingActionButton = { Fab(addAction = { viewModel.addAisle(id, getStoreNameString())}) },
        content = { innerPadding ->
            SwipeReorderableList(
                modifier = Modifier.padding(innerPadding),
                listItems = aisles,
                toString = {it.locationName},
                getKey = {it.locationId},
                onDragStopped = { l -> viewModel.syncAisles(l) },
                leftAction = editAction,
                rightAction = delAction
            )
        }
    )

}