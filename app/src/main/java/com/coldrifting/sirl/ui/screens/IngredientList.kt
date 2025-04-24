package com.coldrifting.sirl.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.ui.components.dialogs.AlertDialog
import com.coldrifting.sirl.ui.components.AppNavBar
import com.coldrifting.sirl.ui.components.swipe.SwipeList
import com.coldrifting.sirl.ui.components.dialogs.TextDialog
import com.coldrifting.sirl.ui.components.AppTopBar
import com.coldrifting.sirl.ui.components.swipe.swipeDeleteAction
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.objects.ItemWithAisleName
import com.coldrifting.sirl.routes.RouteIngredientDetails
import com.coldrifting.sirl.routes.RouteStoreList
import com.coldrifting.sirl.routes.top.TopLevelRoute.Companion.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlinx.coroutines.launch

fun Modifier.positionAwareImePadding() = composed {
    var consumePadding by remember { mutableIntStateOf(0) }
    onGloballyPositioned { coordinates ->
        val rootCoordinate = coordinates.findRootCoordinates()
        val bottom = coordinates.positionInWindow().y + coordinates.size.height

        consumePadding = (rootCoordinate.size.height - bottom).toInt()
    }
        .consumeWindowInsets(PaddingValues(bottom = (consumePadding / LocalDensity.current.density).dp))
        .imePadding()
}

@Composable
fun IngredientList(
    navHostController: NavHostController,
    addItem: (String) -> Unit,
    checkDeleteItem: suspend (Int) -> List<String>,
    deleteItem: (Int) -> Unit,
    items: List<ItemWithAisleName>,
    setItemSort: () -> Unit,
    onFilterTextChanged: (String) -> Unit,
    searchText: String,
    sortMode: String
) {
    var coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteDialogConflicts by remember { mutableStateOf(listOf<String>()) }
    var deleteDialogItem by remember { mutableIntStateOf(-1) }
    if (showDeleteDialog) {
        AlertDialog(
            title = "Delete Ingredient?",
            confirmText = "Delete",
            onConfirm = {deleteItem(deleteDialogItem)},
            onDismiss = {deleteDialogConflicts = listOf<String>(); deleteDialogItem = -1; showDeleteDialog = false}
        ) {
            Text("The deleted ingredient will be removed from these recipes:\n\n" +
                deleteDialogConflicts.reduce { initial, element -> "$initial\n$element" }
            )
        }
    }

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(title = "Add Ingredient",
            placeholder = "Ingredient Name",
            action = "Add",
            onSuccess = { addItem(it) },
            onDismiss = { showNewAlertDialog = false })
    }

    Scaffold(
        topBar = {
            AppTopBar(
                navHostController = navHostController,
                title = "Ingredients by $sortMode",
                titleAction = setItemSort,
                topAction = {
                    IconButton(onClick = { navHostController.navigate(RouteStoreList) }) {
                        Icon(Icons.Default.Place, "Stores")
                    }
                }
            )
        },
        bottomBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
                BottomAppBar(modifier = Modifier
                    .then(if (showNewAlertDialog) Modifier else Modifier.positionAwareImePadding())
                    .padding(bottom = 4.dp),
                    windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                    actions = {
                        Spacer(Modifier.weight(1f))
                        OutlinedTextField(value = searchText,
                            singleLine = true,
                            trailingIcon = {
                                val keyboardController = LocalSoftwareKeyboardController.current
                                IconButton(
                                    enabled = searchText.trim() != "",
                                    onClick = { onFilterTextChanged(""); keyboardController?.hide() }) {
                                    if (searchText.trim() == "") {
                                        Icon(Icons.Default.Search, "Search")
                                    } else {
                                        Icon(Icons.Default.Clear, "Clear Search")
                                    }
                                }
                            },
                            placeholder = { Text("Filter Items") },
                            onValueChange = { onFilterTextChanged(it) })
                        Spacer(Modifier.weight(1f))
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showNewAlertDialog = true },
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Default.Add, "Add Item")
                        }
                    })

                AppNavBar(navHostController, routeIngredients)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = { innerPadding ->
            SwipeList(
                modifier = Modifier.padding(innerPadding),
                listItems = items,
                getKey = { it.item.itemId },
                rightAction = swipeDeleteAction {
                    coroutineScope.launch {
                        val conflicts = checkDeleteItem(it)
                        if (conflicts.isEmpty()) {
                            deleteItem(it)
                        }
                        else {
                            deleteDialogConflicts = conflicts
                            deleteDialogItem = it
                            showDeleteDialog = true
                        }
                    }
                },
                tapAction = { navHostController.navigate(RouteIngredientDetails(it)) },
                rowPadding = PaddingValues(start = 0.dp, end = 16.dp)
            ) {
                val tempColor = it.item.getTempColor()
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .drawBehind {
                            drawRect(size = size, color = tempColor)
                        })
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = it.item.itemName,
                    fontSize = 18.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = it.aisleName ?: "(No Aisle Set)", fontSize = 12.sp
                )
            }
        }
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IngredientListPreview() {
    SIRLTheme {
        IngredientList(
            navHostController = rememberNavController(),
            addItem = {},
            deleteItem = {},
            items =
            listOf(
                ItemWithAisleName(Item(3, "Red Wine Vinegar"), "Aisle 02"),
                ItemWithAisleName(Item(44, "Alfredo Sauce"), "Aisle 03"),
                ItemWithAisleName(Item(74, "Crushed Red Pepper"), null),
                ItemWithAisleName(Item(103, "Powdered Sugar"), "Aisle 06")
            ),
            setItemSort = {},
            onFilterTextChanged = {},
            searchText = "Searching",
            sortMode = "Name",
            checkDeleteItem = { i: Int -> listOf("") }
        )
    }
}