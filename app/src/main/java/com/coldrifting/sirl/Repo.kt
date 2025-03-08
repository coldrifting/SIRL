package com.coldrifting.sirl

import com.coldrifting.sirl.entities.Store
import com.coldrifting.sirl.entities.StoreLocation
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Repo {
    private var _indexStores = 2
    private var _indexAisles = 6

    private val _stores = initStores()
    val stores = _stores.asStateFlow()

    private val _allStoreLocations = initStoreLocations()

    // Store to use for editing aisle info
    private val _currentStoreId = MutableStateFlow(0)
    val currentStoreId = _currentStoreId.asStateFlow()

    val currentStoreLocations = combine(_allStoreLocations, _currentStoreId, listOf(),
        { list: List<StoreLocation>, filter: Int -> list.filter{sf -> sf.storeId == filter} })

    // Store to use for cart, ingredients
    private val _selectedStoreId = MutableStateFlow(0)
    val selectedStoreId = _selectedStoreId.asStateFlow()

    fun selectStore(storeId: Int) {
        _selectedStoreId.value = storeId
    }

    fun getStore(storeId: Int): Store? {
        return _stores.value.firstOrNull { s -> s.storeId == storeId }
    }

    fun addStore(storeName: String) {
        _stores.update { oldList ->
            val newList = oldList.toMutableList().apply {
                add(Store(_indexStores++, storeName))
            }
            newList
        }
    }

    fun renameStore(storeId: Int, newStoreName: String) {
        val store = _stores.value.firstOrNull {s -> s.storeId == storeId} ?: return
        val index = _stores.value.indexOf(store)
        _stores.update { oldList ->
            val newList = oldList.toMutableList().apply {
                set(index, Store(store.storeId, newStoreName))
            }
            newList
        }
    }

    fun deleteStore(storeId: Int) {
        val store = _stores.value.firstOrNull {s -> s.storeId == storeId} ?: return
        _stores.update { oldList ->
            val newList = oldList.toMutableList().apply {
                remove(store)
            }
            newList
        }

        val storeLocations = _allStoreLocations.value.filter {s -> s.storeId == storeId}
        _allStoreLocations.update { oldList ->
            val newList = oldList.toMutableList().apply {
                removeAll(storeLocations)
            }
            newList
        }
    }


    fun setCurrentStoreForEdit(storeId: Int) {
        _currentStoreId.value = storeId
    }

    fun addStoreLocation(storeId: Int, storeLocationName: String) {
        _currentStoreId.value = storeId

        val aisle = currentStoreLocations.value.firstOrNull{s -> s.storeId == storeId && s.locationName == storeLocationName}
        if (aisle != null) return

        _allStoreLocations.update { oldList ->
            val newList = oldList.toMutableList().apply {
                add(StoreLocation(_indexAisles++, storeId, storeLocationName))
            }
            newList
        }
    }

    fun renameStoreLocation(storeId: Int, locationId: Int, storeLocationName: String) {
        _currentStoreId.value = storeId

        val aisleToRename = currentStoreLocations.value.firstOrNull{s -> s.storeId == storeId && s.locationId == locationId} ?: return

        // Ensure aisle names are unique - Needed?
        val aisle = currentStoreLocations.value.firstOrNull{s -> s.locationName == storeLocationName}
        if (aisle != null) return

        val index = _allStoreLocations.value.indexOf(aisleToRename)
        _allStoreLocations.update { oldList ->
            val newList = oldList.toMutableList().apply {
                set(index, StoreLocation(locationId, storeId, storeLocationName))
            }
            newList
        }
    }

    fun replaceStoreLocations(locations: List<StoreLocation>) {
        _allStoreLocations.update { oldList ->
            val newList = oldList.toMutableList().apply {
                removeAll(locations)
                addAll(locations)
            }
            newList
        }
    }

    fun deleteStoreLocation(storeId: Int, locationId: Int) {
        _currentStoreId.value = storeId

        val aisleToDelete = currentStoreLocations.value.firstOrNull{s -> s.storeId == storeId && s.locationId == locationId} ?: return

        _allStoreLocations.update { oldList ->
            val newList = oldList.toMutableList().apply {
                remove(aisleToDelete)
            }
            newList
        }
    }

    // TODO - Replace with JSON contents
    private fun initStores(): MutableStateFlow<List<Store>> {
        return MutableStateFlow(listOf(
            Store(0, "Macey's (1700 S)"),
            Store(1, "WinCo (2100 S)")
        ))
    }

    private fun initStoreLocations(): MutableStateFlow<List<StoreLocation>> {
        return MutableStateFlow(listOf(
            StoreLocation(0, 0, "Produce"),
            StoreLocation(1, 0, "Bakery"),
            StoreLocation(2, 0, "Aisle 2"),
            StoreLocation(3, 0, "Aisle 11"),
            StoreLocation(4, 1, "Aisle 5"),
            StoreLocation(5, 1, "Aisle 4"),
        ))
    }

    private fun <T1, T2, R> combine(flow: StateFlow<T1>, flow2: StateFlow<T2>, initialValue: R, transform: suspend (a: T1, b: T2) -> R): StateFlow<R> {
        return object : StateFlow<R> {
            private val mutex = Mutex()
            override var value: R = initialValue

            override val replayCache: List<R> get() = listOf(value)

            override suspend fun collect(collector: FlowCollector<R>): Nothing {
                combine(flow, flow2, transform)
                    .onStart { emit(initialValue) }
                    .collect {
                        mutex.withLock {
                            value = it
                            collector.emit(it)
                        }
                    }
                error("This exception is needed to 'return' Nothing. It won't be thrown (collection of StateFlow will never end)")
            }
        }
    }
}