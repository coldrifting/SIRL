package com.coldrifting.sirl.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.components.AlertDialog
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.Section
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.data.entities.CartAisleEntry
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeCart
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlinx.coroutines.launch

@Composable
fun Cart(
    navHostController: NavHostController,
    getShoppingList: suspend () -> List<CartAisleEntry>?
) {
    // TODO - save list state when app restarts
    var list by rememberSaveable { mutableStateOf<List<CartAisleEntry>?>(null) }
    var listX = list

    var coroutineScope = rememberCoroutineScope()
    fun resetList() {
        coroutineScope.launch {
            list = getShoppingList.invoke()
        }
    }

    var complete = list?.flatMap { i -> i.entries }?.all { e -> e.checked } == true

    var showResetListDialog by remember {mutableStateOf(false)}
    if (showResetListDialog) {
        AlertDialog(
            title = "Regenerate Shopping List?",
            onConfirm = {resetList()},
            onDismiss = {showResetListDialog = false}) {
            Text("All checked values will be lost")
        }
    }


    fun updateList() {
        if (list?.flatMap { i -> i.entries }?.any { e -> e.checked } == true) {
            showResetListDialog = true
        }
        else {
            resetList()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navHostController,
                "Cart" + if (complete) " (Complete)" else "",
                topAction = if (list != null) {{
                    IconButton(onClick = ::updateList) {
                        Icon(
                            Icons.Default.Refresh,
                            "Regenerate List"
                        )
                    }
                }} else null)
        },
        bottomBar = { NavBar(navHostController, routeCart) },
        content = { innerPadding ->
            if (listX != null) {
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    itemsIndexed(listX) { index, entry ->
                        Section(
                            title = entry.aisleName,
                            collapsable = true,
                            startExpanded = true // TODO - Handle automatic section collapse
                        ) {
                            entry.entries.forEach { subEntry ->

                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            var newList = list?.toMutableList()
                                            if (newList == null) {
                                                return@clickable
                                            }

                                            var index = newList.indexOf(entry)
                                            var newList2 = entry.entries.toMutableList()
                                            var index2 = newList2.indexOf(subEntry)
                                            newList2[index2] =
                                                subEntry.copy(checked = !subEntry.checked)
                                            newList[index] = CartAisleEntry(
                                                entry.aisleId,
                                                entry.aisleName,
                                                newList2
                                            )
                                            list = newList
                                        }
                                        .padding(vertical = 12.dp)
                                        .padding(start = 32.dp, end = 16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(subEntry.itemName)
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = subEntry.amount,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    Checkbox(
                                        modifier = Modifier.padding(0.dp),
                                        checked = subEntry.checked,
                                        onCheckedChange = null
                                    )
                                }

                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = ::updateList) {
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
            getShoppingList = { listOf() })
    }
}