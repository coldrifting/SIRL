package com.coldrifting.sirl.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.ui.components.dialogs.AlertDialog
import com.coldrifting.sirl.ui.components.checklist.CartChecklist
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.routes.top.TopLevelRoute.Companion.routeCart
import com.coldrifting.sirl.ui.theme.SIRLTheme

@Composable
fun Cart(
    navHostController: NavHostController,
    list: List<ChecklistHeader>?,
    getShoppingList: () -> Unit,
    onHeaderClicked: (Int) -> Unit,
    onItemClicked: (Int, Int) -> Unit
) {
    var complete = list?.flatMap { i -> i.items }?.all { e -> e.checked } == true

    var showNoLocationDialog by remember { mutableStateOf(false) }
    var noLocationItems by remember { mutableStateOf(listOf<String>()) }
    if (showNoLocationDialog) {
        AlertDialog(
            title = "Missing Item Locations",
            showDismissButton = false,
            confirmText = "OK",
            onConfirm = {},
            onDismiss = { showNoLocationDialog = false }
        ) {
            Text("The following items do not have aisle information entered " +
                    "for the currently selected store: \n")
            Column {
                noLocationItems.forEachIndexed { index, item ->
                    if (index < 5) {
                        Text(item)
                    }
                    else if (index == 5) {
                        Text("...")
                    }
                    else {
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    if (list != null) {
        noLocationItems = list.find { i -> i.id == -1 }?.items?.map{ i -> i.name} ?: listOf()
        if (noLocationItems.isNotEmpty()) {
            LaunchedEffect(noLocationItems) {
                showNoLocationDialog = true
            }
        }
    }

    var showResetListDialog by remember { mutableStateOf(false) }
    if (showResetListDialog) {
        AlertDialog(
            title = "Regenerate Shopping List?",
            onConfirm = { getShoppingList.invoke() },
            onDismiss = { showResetListDialog = false }
        ) {
            Text("All checked values will be lost")
        }
    }

    var total = list?.flatMap{ i -> i.items}?.size
    var inProgress = list?.flatMap{ i -> i.items}?.count{ e -> e.checked }

    Scaffold(
        topBar = {
            AppTopBar(
                navHostController,
                "Cart" + if (complete) " (Complete)" else if (total != null) " ($inProgress / $total)" else "",
                topAction = if (list != null) {
                    {
                        IconButton(onClick = {
                            if (list.flatMap { i -> i.items }.any { e -> e.checked } == true) {
                                showResetListDialog = true
                            } else {
                                getShoppingList.invoke()
                            }
                        }) {
                            Icon(
                                Icons.Default.Refresh,
                                "Regenerate List"
                            )
                        }
                    }
                } else null
            )
        },
        bottomBar = { AppNavBar(navHostController, routeCart) },
        content = { innerPadding ->
            if (list != null) {
                Box(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    CartChecklist(
                        entries = list,
                        onHeaderClicked = onHeaderClicked,
                        onItemClicked = onItemClicked
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = getShoppingList) {
                        Text("Generate List")
                    }
                }
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CartPreview() {
    SIRLTheme {
        Cart(
            navHostController = rememberNavController(),
            list = listOf(),
            getShoppingList = { },
            onHeaderClicked = {i -> },
            onItemClicked = {i, i2 -> }
        )
    }
}