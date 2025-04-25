package com.coldrifting.sirl.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.objects.ItemWithAisleName
import com.coldrifting.sirl.data.objects.RecipeTreeItem
import com.coldrifting.sirl.data.objects.RecipeTreeItemPrep
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemAisle
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.repo.utils.toListStateFlow
import com.coldrifting.sirl.repo.utils.toNullableStateFlow
import com.coldrifting.sirl.repo.utils.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ItemRepo(
    private val db: Database,
    private val scope: CoroutineScope,
    private val selectedStoreId: StateFlow<Int?>
) {
    enum class ItemsSortingMode {
        Name,
        Aisle,
        Temp
    }
    val allItemsWithPrep: StateFlow<List<RecipeTreeItem>> =
        db.itemsQueries.getAllItemsWithPrep().toListStateFlow(scope) { x ->
            x.map { y ->
                RecipeTreeItem(
                    itemId = y.itemId,
                    itemName = y.itemName,
                    itemPrep = if (y.itemPrepId != null && y.prepName != null) RecipeTreeItemPrep(
                        y.itemPrepId,
                        y.prepName
                    )
                    else null,
                    defaultUnits = y.defaultUnits
                )
            }
        }

    private val _itemsFilterTextState = MutableStateFlow("")
    val itemsFilterTextState = _itemsFilterTextState.asStateFlow()

    fun setFilterText(searchText: String) {
        _itemsFilterTextState.update {
            searchText
        }
    }

    private val _itemsSortingModeState = MutableStateFlow(ItemsSortingMode.Name)
    val itemsSortingModeState = _itemsSortingModeState.asStateFlow()
    fun toggleItemsSortingMode() {
        _itemsSortingModeState.update { oldValue ->
            when (oldValue) {
                ItemsSortingMode.Name -> ItemsSortingMode.Aisle
                ItemsSortingMode.Aisle -> ItemsSortingMode.Temp
                ItemsSortingMode.Temp -> ItemsSortingMode.Name
            }
        }
    }

    data class FilterCombine(
        val filterText: String,
        val curStore: Int,
        val sortMode: ItemsSortingMode
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemsWithFilter = combine(
        itemsFilterTextState,
        selectedStoreId.map { s -> s ?: -1 },
        itemsSortingModeState
    ) { filterText: String, curStore: Int, sortMode: ItemsSortingMode ->
        FilterCombine(filterText, curStore, sortMode)
    }.flatMapLatest {
        db.itemsQueries.getAllItemDetailsFiltered(
                storeId = it.curStore,
                itemFilter = it.filterText,
                sortingMode = it.sortMode.name).asFlow().mapToList(scope.coroutineContext)
            .map { map ->
                map.toList().map { listItem ->
                    ItemWithAisleName(Item(listItem.itemId, listItem.itemName, listItem.temperature, listItem.defaultUnits), listItem.aisleName)
                }
            }
    }.stateIn(scope, SharingStarted.Companion.Eagerly, emptyList())

    // Items
    fun addItem(itemName: String): Int {
        db.itemsQueries.addItem(itemName, ItemTemp.Ambient, UnitType.Count)
        val itemId = db.itemsQueries.lastInsertRowId().executeAsOne().toInt()
        return itemId
    }

    fun deleteItem(itemId: Int) {
        db.itemsQueries.deleteItem(itemId)
    }

    fun getItem(itemId: Int): StateFlow<Item> {
        return db.itemsQueries.getItem(itemId).toStateFlow(scope, Item()) { item ->
            Item(item.itemId, item.itemName, item.temperature, item.defaultUnits)
        }
    }

    fun setItemName(itemId: Int, itemName: String) {
        db.itemsQueries.renameItem(itemName, itemId)
    }

    fun setItemTemp(itemId: Int, itemTemp: ItemTemp) {
        db.itemsQueries.setItemTemp(itemTemp, itemId)
    }

    fun setItemDefaultUnits(itemId: Int, unitType: UnitType) {
        db.itemsQueries.setItemUnits(unitType, itemId)
    }

    fun getItemAisle(itemId: Int): StateFlow<ItemAisle?> {
        return db.itemsQueries.getItemAisleAtStore(itemId, selectedStoreId.value?.toLong()).toNullableStateFlow(scope, null) { itemAisle ->
            if (itemAisle == null)
                null
            else
                ItemAisle(
                    itemAisle.itemId,
                    itemAisle.storeId,
                    itemAisle.aisleId,
                    itemAisle.bay)
        }
    }

    fun updateItemAisle(itemId: Int, aisleId: Int, bayType: BayType) {
        val selectedStore = selectedStoreId.value
        if (selectedStore != null) {
            db.itemsQueries.updateItemAisle(itemId, selectedStore, aisleId, bayType)
        }
    }

    fun getItemPreparations(itemId: Int): StateFlow<List<ItemPrep>> {
        return db.itemsQueries.getItemPrep(itemId).toListStateFlow(scope) { l ->
            l.map { i ->
                ItemPrep(i.itemPrepId, i.itemId, i.prepName)
            }
        }
    }

    fun addItemPrep(itemId: Int, prepName: String) {
        db.itemsQueries.addItemPrep(itemId, prepName)
    }

    fun updateItemPrep(prepId: Int, prepName: String) {
        db.itemsQueries.renameItemPrep(prepName, prepId)
    }

    fun deleteItemPrep(prepId: Int) {
        db.itemsQueries.deleteItemPrep(prepId)
    }

    fun getUsedItems(itemId: Int): List<String> {
        return db.itemsQueries.getRecipesContainingItem(itemId).executeAsList()
    }

    fun getUsedItemPreps(itemPrepId: Int): List<String> {
        return db.itemsQueries.getRecipesContainingItemPrep(itemPrepId).executeAsList()
    }
}