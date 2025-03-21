package com.coldrifting.sirl.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
import com.coldrifting.sirl.entities.types.ItemCategory
import com.coldrifting.sirl.entities.types.ItemCategory.Companion.getIcon
import com.coldrifting.sirl.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    title: String,
    addItem: (String) -> Unit,
    deleteItem: (Int) -> Unit,
    getItems: (String) -> StateFlow<List<Item>>
) {
    var searchText by remember { mutableStateOf("") }
    val flow = remember(searchText) { getItems(searchText.trim()) }
    val itemList by flow.collectAsState()

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(title = "Add Ingredient",
            placeholder = "Ingredient Name",
            action = "Add",
            onSuccess = { addItem(it) },
            onDismiss = { showNewAlertDialog = false })
    }

    Scaffold(topBar = { TopBar(navHostController, title) }, bottomBar = {
        Column {
            val modifierIme = if (showNewAlertDialog) {
                Modifier
            }
            else {
                Modifier.positionAwareImePadding()
            }

            BottomAppBar(modifier = modifierIme,
                windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                actions = {
                    Spacer(Modifier.weight(1f))
                    OutlinedTextField(value = searchText,
                        singleLine = true,
                        trailingIcon = {
                            val keyboardController = LocalSoftwareKeyboardController.current
                            IconButton(enabled = searchText.trim() != "", onClick = { searchText = "" ; keyboardController?.hide() }) {
                                if (searchText.trim() == "") {
                                    Icon(Icons.Default.Search, "Search")
                                }
                                else {
                                    Icon(Icons.Default.Clear, "Clear Search")
                                }
                            }
                        },
                        placeholder = { Text("Filter Items") },
                        onValueChange = { searchText = it })
                    Spacer(Modifier.weight(1f))
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showNewAlertDialog = true },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Default.Add, "Add Item")
                    }
                })

            NavBar(navHostController, routeIngredients)
        }
    }, contentWindowInsets = WindowInsets(0, 0, 0, 0), content = { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SwipeList(listItems = itemList,
                getKey = { it.itemId },
                rowItemLayout = {
                    Icon(
                        imageVector = it.itemCategory.getIcon(),
                        contentDescription = it.itemCategory.toString()
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = it.itemName,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = it.itemCategory.toString(), fontSize = 12.sp
                    )
                },
                leftAction = swipeEditAction { navHostController.navigate(IngredientDetails(it)) },
                rightAction = swipeDeleteAction { deleteItem(it) })
            Spacer(Modifier.weight(1f))
        }
    })
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IngredientListPreview() {
    SIRLTheme {
        IngredientList(navHostController = rememberNavController(),
            title = "Items",
            addItem = {},
            deleteItem = {},
            getItems = {
                MutableStateFlow(
                    listOf(
                        Item(0, "a", ItemCategory.Deli),
                        Item(1, "b", ItemCategory.Deli),
                        Item(2, "c", ItemCategory.Deli),
                        Item(3, "d", ItemCategory.Deli),
                        Item(4, "e", ItemCategory.Deli),
                        Item(5, "f", ItemCategory.Deli),
                        Item(6, "g", ItemCategory.Deli),
                        Item(7, "h", ItemCategory.Deli),
                        Item(8, "i", ItemCategory.Deli),
                        Item(9, "j", ItemCategory.Deli),
                        Item(10, "k", ItemCategory.Deli),
                        Item(11, "l", ItemCategory.Deli),
                        Item(12, "m", ItemCategory.Deli),
                        Item(13, "n", ItemCategory.Deli),
                        Item(14, "o", ItemCategory.Deli),
                        Item(15, "p", ItemCategory.Deli),
                    )
                ).asStateFlow()
            })
    }
}