package com.coldrifting.sirl

import android.content.Context
import android.util.Log
import com.coldrifting.sirl.data.access.AisleDAO
import com.coldrifting.sirl.data.access.ItemAisleDAO
import com.coldrifting.sirl.data.access.ItemDAO
import com.coldrifting.sirl.data.access.StoreDAO
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.data.entities.helper.ItemWithAisleName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
import java.util.concurrent.Executors

class AppRepository(
    private val scope: CoroutineScope,
    private val storeDao: StoreDAO,
    private val aisleDao: AisleDAO,
    private val itemDao: ItemDAO,
    private val itemAisleDao: ItemAisleDAO,
    private val context: Context
) {

    // StateFlows
    val allStores = storeDao.all().toStateFlow()
    private val allAisles = aisleDao.all().toStateFlow()
    private val allItems = itemDao.all().toStateFlow()

    fun getAislesAtStore(storeId: Int): StateFlow<List<Aisle>> {
        return aisleDao.all(storeId).toStateFlow()
    }

    // Store Selection
    val selectedStoreId =
        context.settingsDataStore.data.map { settings -> settings.storeSelection }.toStateFlow(-1)

    fun selectStore(storeId: Int) {
        scope.launch {
            context.settingsDataStore.updateData { t ->
                t.toBuilder().setStoreSelection(storeId).build()
            }
        }
    }

    fun trySelectStore() {
        if (allStores.value.firstOrNull { s -> s.storeId == selectedStoreId.value } == null) {
            ioThread {
                val newSelectedStoreId = storeDao.firstStoreIdOrDefault()
                if (newSelectedStoreId != -1) {
                    selectStore(newSelectedStoreId)
                } else {
                    Log.d("TEST", "Unable to select a store")
                }
            }
        }
    }

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
        selectedStoreId,
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
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Stores
    fun addStore(storeName: String) {
        ioThread {
            storeDao.insert(Store(storeName = storeName))
            if (selectedStoreId.value == -1) {
                selectStore(storeDao.firstStoreIdOrDefault())
            }
        }
    }

    fun renameStore(storeId: Int, newName: String) {
        ioThread {
            storeDao.insert(Store(storeId, newName))
        }
    }

    fun deleteStore(storeId: Int) {
        ioThread {
            storeDao.delete(Store(storeId = storeId, storeName = ""))
            if (selectedStoreId.value == storeId) {
                selectStore(storeDao.firstStoreIdOrDefault())
            }
        }
    }

    fun getStoreName(storeId: Int): String {
        return allStores.value.firstOrNull { s -> s.storeId == storeId }?.storeName ?: ""
    }

    // Aisles
    fun addAisle(storeId: Int, aisleName: String) {
        ioThread {
            val maxSortValue = aisleDao.maxSortingPrefixValue()
            aisleDao.insert(
                Aisle(
                    storeId = storeId,
                    aisleName = aisleName,
                    sortingPrefix = maxSortValue + 1
                )
            )
        }
    }

    fun renameAisle(aisleId: Int, newAisleName: String) {
        ioThread {
            aisleDao.updateAisleName(aisleId, newAisleName)
        }
    }

    fun deleteAisle(aisleId: Int) {
        ioThread {
            aisleDao.delete(Aisle(aisleId = aisleId, aisleName = "", storeId = -1))
        }
    }

    fun reorderAisles(items: List<Aisle>) {
        ioThread {
            val list = getReorderedItems(items)
            aisleDao.insert(list)
        }
    }

    fun getAisleName(aisleId: Int): String {
        return allAisles.value.firstOrNull { a -> a.aisleId == aisleId }?.aisleName ?: ""
    }

    // Items
    fun addItem(itemName: String) {
        ioThread {
            itemDao.insert(Item(itemName = itemName))
        }
    }

    fun deleteItem(itemId: Int) {
        ioThread {
            itemDao.delete(
                Item(
                    itemId = itemId,
                    itemName = ""
                )
            )
        }
    }

    fun getItemName(itemId: Int): String {
        return allItems.value.firstOrNull { i -> i.itemId == itemId }?.itemName ?: ""
    }

    private fun ioThread(f: () -> Unit) {
        Executors.newSingleThreadExecutor().execute(f)
    }

    private fun getReorderedItems(items: List<Aisle>): List<Aisle> {
        val list = mutableListOf<Aisle>()
        for ((count, storeLocation) in items.withIndex()) {
            list.add(
                Aisle(
                    aisleId = storeLocation.aisleId,
                    storeId = storeLocation.storeId,
                    aisleName = storeLocation.aisleName,
                    sortingPrefix = count
                )
            )
        }
        return list
    }

    private fun <T> Flow<List<T>>.toStateFlow(): StateFlow<List<T>> {
        return this.toStateFlow(listOf())
    }

    private fun <T> Flow<T>.toStateFlow(defaultVal: T): StateFlow<T> {
        return this.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = defaultVal
        )
    }
}