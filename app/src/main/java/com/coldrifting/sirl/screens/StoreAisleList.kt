package com.coldrifting.sirl.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.coldrifting.sirl.AppViewModel
import com.coldrifting.sirl.components.SwipeReorderableList
import com.coldrifting.sirl.components.SwipeTapAction
import com.coldrifting.sirl.ui.theme.DelColor
import com.coldrifting.sirl.ui.theme.EditColor

@Composable
fun StoreAisleList(id: Int, viewModel: AppViewModel) {

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

    val aisles by viewModel.aisles.collectAsState()
    val aisleList = aisles[id]?.toList() ?: return
    SwipeReorderableList(
        listItems = aisleList,
        onMove = {from, to -> viewModel.swapAisles(id, from, to)},
        leftAction = editAction,
        rightAction = delAction,
        onDragStopped = {}
    )
}