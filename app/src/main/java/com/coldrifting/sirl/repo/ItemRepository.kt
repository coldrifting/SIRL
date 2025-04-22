package com.coldrifting.sirl.repo

import com.coldrifting.sirl.data.access.ItemAisleDAO
import com.coldrifting.sirl.data.access.ItemDAO
import com.coldrifting.sirl.data.access.ItemPrepDAO
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemAisle
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.helper.ItemWithAisleName
import com.coldrifting.sirl.util.toStateFlow
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
import kotlinx.coroutines.launch

class ItemRepository(
    private val itemDao: ItemDAO,
    private val itemPrepDao: ItemPrepDAO,
    private val itemAisleDao: ItemAisleDAO,
    private val scope: CoroutineScope,
    val selectedStoreId: StateFlow<Int?>
) {
    val allItemsWithPrep = itemPrepDao.getAllItemsAndPreps().map { x -> x.map{ y -> y.toItemX()}}.toStateFlow(scope)

    enum class ItemsSortingMode {
        Name,
        Aisle,
        Temp
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
        itemAisleDao.details(it.curStore, it.sortMode.name, it.filterText)
            .map { map ->
                map.toList().map { listItem ->
                    ItemWithAisleName(listItem.first, listItem.second?.aisleName)
                }
            }
    }.stateIn(scope, SharingStarted.Companion.Eagerly, emptyList())

    // Items
    fun addItem(itemName: String) {
        scope.launch {
            itemDao.insert(Item(itemName = itemName))
        }
    }

    fun deleteItem(itemId: Int) {
        scope.launch {
            itemDao.delete(
                Item(
                    itemId = itemId,
                    itemName = ""
                )
            )
        }
    }

    fun getItem(itemId: Int): StateFlow<Item> {
        return itemDao.getItem(itemId).toStateFlow(scope, Item(itemName = " "))
    }

    fun setItemTemp(itemId: Int, itemTemp: ItemTemp) {
        scope.launch {
            itemDao.updateTemp(itemId, itemTemp)
        }
    }

    fun setItemName(itemId: Int, itemName: String) {
        scope.launch {
            itemDao.updateName(itemId, itemName)
        }
    }

    fun getItemAisle(itemId: Int): StateFlow<ItemAisle?> {
        return itemAisleDao.getByItem(itemId, selectedStoreId.value).toStateFlow(scope, null)
    }

    fun updateItemAisle(itemId: Int, aisleId: Int, bayType: BayType) {
        scope.launch {
            val selectedStore = selectedStoreId.value
            if (selectedStore != null) {
                itemAisleDao.insert(ItemAisle(itemId, selectedStore, aisleId, bayType))
            }
        }
    }

    fun setItemDefaultUnits(itemId: Int, unitType: UnitType) {
        scope.launch {
            itemDao.updateItemDefaultUnits(itemId, unitType)
        }
    }

    fun getItemPreparations(itemId: Int): StateFlow<List<ItemPrep>> {
        return itemPrepDao.getItemPreps(itemId).toStateFlow(scope)
    }

    fun addItemPrep(itemId: Int, prepName: String) {
        scope.launch {
            itemPrepDao.insert(ItemPrep(itemId = itemId, prepName = prepName))
        }
    }

    fun updateItemPrep(prepId: Int, prepName: String) {
        scope.launch {
            itemPrepDao.update(prepId, prepName)
        }
    }

    fun deleteItemPrep(prepId: Int) {
        scope.launch {
            itemPrepDao.delete(ItemPrep(itemPrepId = prepId, itemId = -1, prepName = "DELETE"))
        }
    }


    suspend fun getUsedItems(itemId: Int): List<String> {
        return itemDao.getUsedItems(itemId)
    }

    suspend fun getUsedItemPreps(itemPrepId: Int): List<String> {
        return itemDao.getUsedItemPreps(itemPrepId)
    }
}