package com.coldrifting.sirl.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.coldrifting.sirl.data.TopLevelRoute.Companion.routeCart
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.checklist.CartChecklist
import com.coldrifting.sirl.ui.components.dialogs.AlertDialog

@Composable
fun CartList(
    navHostController: NavHostController,
    list: List<ChecklistHeader>,
    locationWarningItems: List<String>,
    dismissLocationWarning: () -> Unit,
    onHeaderClicked: (Int) -> Unit,
    onItemClicked: (Int, Int) -> Unit
) {
    var complete = list.flatMap { i -> i.items }.all { e -> e.checked } == true

    var showNoLocationDialog by remember(locationWarningItems) { mutableStateOf(locationWarningItems.isNotEmpty()) }
    if (showNoLocationDialog) {
        AlertDialog(
            title = "Missing Item Locations",
            showDismissButton = false,
            confirmText = "OK",
            onDismiss = dismissLocationWarning
        ) {
            Text(
                "The following items do not have aisle information entered " +
                        "for the currently selected store: \n"
            )
            Column {
                locationWarningItems.forEachIndexed { index, item ->
                    if (index < 5) {
                        Text(item)
                    } else if (index == 5) {
                        Text("...")
                    } else {
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    var total = list.flatMap { i -> i.items }.size
    var inProgress = list.flatMap { i -> i.items }.count { e -> e.checked }

    Scaffold(
        topBar = {
            AppTopBar(
                navHostController,
                "Cart (${if(complete) "Complete" else "$inProgress / $total"})"
            )
        },
        bottomBar = { AppNavBar(navHostController, routeCart) },
        content = { innerPadding ->
            CartChecklist(
                modifier = Modifier.padding(innerPadding),
                entries = list,
                onHeaderClicked = onHeaderClicked,
                onItemClicked = onItemClicked
            )
        }
    )
}