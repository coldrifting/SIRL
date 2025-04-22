package com.coldrifting.sirl.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.coldrifting.sirl.components.AlertDialog
import com.coldrifting.sirl.components.NavBar
import com.coldrifting.sirl.components.Section
import com.coldrifting.sirl.components.SwipeList
import com.coldrifting.sirl.components.TextDialog
import com.coldrifting.sirl.components.TextFieldWithDebounce
import com.coldrifting.sirl.components.TopBar
import com.coldrifting.sirl.components.swipeDeleteAction
import com.coldrifting.sirl.components.swipeEditAction
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.data.entities.joined.ItemAisle
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.routes.TopLevelRoute.Companion.routeIngredients
import com.coldrifting.sirl.ui.theme.SIRLTheme
import kotlinx.coroutines.launch

@Composable
fun IngredientDetails(
    navHostController: NavHostController,
    item: Item,
    itemAisle: ItemAisle?,
    aisles: List<Aisle>,
    stores: List<Store>,
    currentStore: Store,
    prep: List<ItemPrep>,
    setStore: (Int) -> Unit,
    setItemName: (Int, String) -> Unit,
    setItemTemp: (Int, ItemTemp) -> Unit,
    setItemAisle: (Int, Int, BayType) -> Unit,
    setItemDefaultUnits: (Int, UnitType) -> Unit,
    addPrep: (Int, String) -> Unit,
    updatePrep: (Int, String) -> Unit,
    deletePrep: (Int) -> Unit,
    checkDeletePrep: suspend (Int) -> List<String>,
) {
    var coroutineScope = rememberCoroutineScope()

    var lastTextValue by remember { mutableStateOf("") }
    var listItem by remember { mutableIntStateOf(-1) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteDialogConflicts by remember { mutableStateOf(listOf<String>()) }
    var deleteDialogItemPrepId by remember { mutableIntStateOf(-1) }
    if (showDeleteDialog) {
        AlertDialog(
            title = "Delete Ingredient Preparation?",
            confirmText = "Delete",
            onConfirm = { deletePrep(deleteDialogItemPrepId) },
            onDismiss = {
                deleteDialogConflicts = listOf<String>(); deleteDialogItemPrepId =
                -1; showDeleteDialog = false
            }
        ) {
            Text(
                "The ingredient and preparation will be removed from these recipes:\n\n" +
                        deleteDialogConflicts.reduce { initial, element -> "$initial\n$element" }
            )
        }
    }

    var showRenameAlertDialog by remember { mutableStateOf(false) }
    if (showRenameAlertDialog) {
        TextDialog(
            title = "Rename Preparation",
            placeholder = "Ingredient Preparation",
            action = "Rename",
            onSuccess = { updatePrep(listItem, it) },
            onDismiss = { showRenameAlertDialog = false },
            defaultValue = lastTextValue
        )
    }

    var userInteraction by remember(item.itemId) { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    var showNewAlertDialog by remember { mutableStateOf(false) }
    if (showNewAlertDialog) {
        TextDialog(
            title = "Add Preparation",
            placeholder = "Ingredient Preparation",
            action = "Add",
            onSuccess = {
                userInteraction = true
                addPrep(item.itemId, it)
            },
            onDismiss = { showNewAlertDialog = false }
        )
    }

    LaunchedEffect(prep) {
        if (userInteraction) {
            scrollState.scrollTo(Int.MAX_VALUE)
        }
    }

    Scaffold(
        topBar = { TopBar(navHostController, "Ingredient Details") },
        bottomBar = { NavBar(navHostController, routeIngredients) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewAlertDialog = true }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
            ) {
                TextFieldWithDebounce(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    obj = item,
                    label = "Ingredient Name",
                    getId = { it.itemId },
                    getName = { it.itemName },
                    setName = setItemName
                )

                Section(
                    title = "Location"
                ) {
                    DropDown(
                        list = stores,
                        label = "Current Store",
                        toString = { it.storeName },
                        value = currentStore,
                        select = { setStore(it.storeId) }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val aisle =
                            aisles.firstOrNull { a -> a.aisleId == (itemAisle?.aisleId ?: "_") }
                                ?: Aisle(storeId = 0, aisleName = "(Select an Aisle)")

                        DropDown(
                            modifier = Modifier.padding(end = 12.dp),
                            list = aisles,
                            label = "Aisle",
                            width = 260.dp,
                            toString = { it.aisleName },
                            value = aisle,
                            select = {
                                setItemAisle(
                                    item.itemId,
                                    it.aisleId,
                                    itemAisle?.bay ?: BayType.Middle
                                )
                            }
                        )

                        DropDown(
                            list = BayType.entries,
                            label = "Bay",
                            width = 100.dp,
                            value = itemAisle?.bay ?: BayType.Middle,
                            select = {
                                setItemAisle(
                                    item.itemId,
                                    itemAisle?.aisleId ?: aisles[0].aisleId,
                                    it
                                )
                            }
                        )
                    }
                }

                Section(
                    title = "Temperature and Units"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DropDown(
                            modifier = Modifier.padding(bottom = 12.dp),
                            width = 200.dp,
                            list = ItemTemp.entries,
                            value = item.itemTemp,
                            select = { setItemTemp(item.itemId, it) },
                            label = "Ingredient Temperature"
                        )

                        DropDown(
                            modifier = Modifier.padding(bottom = 12.dp),
                            width = 150.dp,
                            list = UnitType.entries,
                            label = "Default Units",
                            value = item.defaultUnits,
                            select = { setItemDefaultUnits(item.itemId, it) }
                        )
                    }
                }

                Section(
                    title = "Preparations"
                ) {
                    val isDefault = prep.isEmpty()
                    val normalColor = MaterialTheme.colorScheme.onSurface
                    val defaultColor = normalColor.copy(alpha = 0.38f)
                    key(isDefault) {
                        SwipeList(
                            listItems = prep.ifEmpty {
                                listOf(
                                    ItemPrep(
                                        itemId = item.itemId,
                                        prepName = "Default"
                                    )
                                )
                            },
                            getKey = { it.itemPrepId },
                            leftAction = if (!isDefault) {
                                swipeEditAction {
                                    listItem = it
                                    lastTextValue =
                                        prep.firstOrNull { p -> p.itemPrepId == it }?.prepName ?: ""
                                    showRenameAlertDialog = true
                                }
                            } else null,
                            rightAction = if (!isDefault) {
                                swipeDeleteAction {
                                    coroutineScope.launch {
                                        val conflicts = checkDeletePrep(it)
                                        if (conflicts.isEmpty()) {
                                            deletePrep(it)
                                        } else {
                                            deleteDialogConflicts = conflicts
                                            deleteDialogItemPrepId = it
                                            showDeleteDialog = true
                                        }
                                    }
                                }
                            } else null,
                            spacing = 12.dp,
                            cornerRadius = 6.dp,
                            scroll = false
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it.prepName,
                                color = if (isDefault) defaultColor else normalColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IngredientDetailsPreview() {
    SIRLTheme {
        IngredientDetails(
            navHostController = rememberNavController(),
            item = Item(itemName = "Aisle"),
            itemAisle = ItemAisle(0, 0, 0),
            setItemTemp = { _, _ -> },
            setItemName = { _, _ -> },
            setItemAisle = { _, _, _ -> },
            setItemDefaultUnits = { _, _ -> },
            aisles = listOf(),
            stores = listOf(Store(1, "Store 1")),
            currentStore = Store(1, "Store 1"),
            setStore = { _ -> },
            prep = listOf(ItemPrep(itemId = 0, prepName = "Prep 1")),
            addPrep = { _, _ -> },
            updatePrep = { _, _ -> },
            deletePrep = { _ -> },
            checkDeletePrep = { i -> listOf("") }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> DropDown(
    modifier: Modifier = Modifier,
    list: List<T>,
    toString: ((T) -> String)? = null,
    select: (T) -> Unit,
    label: String,
    value: T,
    width: Dp? = null
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = if (width != null) modifier.width(width)
        else modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            value = toString?.invoke(value) ?: value.toString(),
            singleLine = true,
            readOnly = true,
            label = { Text(label) },
            onValueChange = {}
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            list.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(toString?.invoke(opt) ?: opt.toString()) },
                    onClick = {
                        expanded = false
                        select(opt)
                    }
                )
            }
        }
    }
}