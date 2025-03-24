package com.coldrifting.sirl.screens

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
import androidx.compose.material.icons.filled.Menu
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
import com.coldrifting.sirl.IngredientDetails
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.SwipeList
import com.coldrifting.sirl.components.TextDialog
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.components.swipeEditAction
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.helper.ItemWithAisleName
import com.coldrifting.sirl.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme

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
    deleteItem: (Int) -> Unit,
    items: List<ItemWithAisleName>,
    setItemSort: () -> Unit,
    onFilterTextChanged: (String) -> Unit,
    searchText: String,
    sortMode: String
) {
    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(title = "Add Ingredient",
            placeholder = "Ingredient Name",
            action = "Add",
            onSuccess = { addItem(it) },
            onDismiss = { showNewAlertDialog = false })
    }

    Scaffold(
        topBar = @Composable {
            TopBar(navHostController, "Ingredients by $sortMode") {
                IconButton(onClick = setItemSort) {
                    Icon(
                        Icons.Default.Menu,
                        "Sorting"
                    )
                }
            }
        },
        bottomBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
                val modifierIme = if (showNewAlertDialog) {
                    Modifier
                } else {
                    Modifier.positionAwareImePadding()
                }

                BottomAppBar(modifier = modifierIme.padding(bottom = 4.dp),
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

                NavBar(navHostController, routeIngredients)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = { innerPadding ->
            SwipeList(modifier = Modifier.padding(innerPadding),
                listItems = items,
                getKey = { it.item.itemId },
                rowPadding = PaddingValues(start = 0.dp, end = 16.dp),
                rowItemLayout = {
                    val tempColor = it.item.getTempColor()
                    Box(modifier = Modifier
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
                },
                leftAction = swipeEditAction { navHostController.navigate(IngredientDetails(it)) },
                rightAction = swipeDeleteAction { deleteItem(it) })
        })
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
        )
    }
}