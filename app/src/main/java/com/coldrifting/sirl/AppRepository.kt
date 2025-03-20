package com.coldrifting.sirl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class AppRepository(val scope: CoroutineScope,
                    private val dao: AppDAO) {
    private val _selectedStoreId = MutableStateFlow(-1)
    val selectedStoreId = _selectedStoreId.asStateFlow()

    private val _currentStoreForEdits = MutableStateFlow(-1)
    val currentStoreForEdits = _currentStoreForEdits.asStateFlow()

    val allStores = dao.allStores().toStateFlow()
    val allLocations = combineStates(
        flow = dao.allLocations().toStateFlow(),
        flow2 = _currentStoreForEdits,
        initialValue = listOf(),
        transform = {a, b -> a.filter{s -> s.storeId == b}}
    )


    fun selectStore(storeId: Int) {
        _selectedStoreId.update {
            return@update storeId
        }
    }

    fun setCurrentStoreForEdit(storeId: Int) {
        _currentStoreForEdits.update {
            return@update storeId
        }
    }

    fun addStore(storeName: String) {
        scope.launch {
            dao.addStore(Store(storeName = storeName))
        }
    }

    fun renameStore(storeId: Int, newName: String) {
        scope.launch {
            dao.addStore(Store(storeId, newName))
        }
    }

    fun deleteStore(storeId: Int) {
        scope.launch {
            dao.deleteStore(StoreId(storeId))
        }
    }

    fun addStoreLocation(storeId: Int, storeLocationName: String) {
        scope.launch {
            val maxSortValue = dao.maxSortingPrefixValue()
            dao.addLocation(StoreLocation(storeId = storeId, locationName = storeLocationName, sortingPrefix = maxSortValue + 1))
        }
    }

    fun renameStoreLocation(locationId: Int, newLocationName: String) {
        scope.launch {
            dao.updateLocation(locationId = locationId, newLocationName = newLocationName)
        }
    }

    fun deleteStoreLocation(locationId: Int) {
        scope.launch {
            dao.deleteLocation(StoreLocationId(locationId))
        }
    }

    fun reorderStoreLocations(items: List<StoreLocation>) {
        scope.launch {
            val list = getReorderedItems(items)
            dao.addLocations(list)
        }
    }

    private fun getReorderedItems(items: List<StoreLocation>) : List<StoreLocation> {
        val list = mutableListOf<StoreLocation>()
        for((count, storeLocation) in items.withIndex()) {
            list.add(StoreLocation(
                locationId = storeLocation.locationId,
                storeId = storeLocation.storeId,
                locationName = storeLocation.locationName,
                sortingPrefix = count))
        }
        return list
    }

    private fun <T1, T2, R> combineStates(flow: StateFlow<T1>, flow2: StateFlow<T2>, initialValue: R, transform: suspend (a: T1, b: T2) -> R): StateFlow<R> {
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

    private fun <T> Flow<List<T>>.toStateFlow(): StateFlow<List<T>> {
        return this.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        ) //start with an empty list
    }

    private fun <T, R> Flow<T>.mapAsStateFlow(
        transform: (T) -> R,
        initialValue: R
    ): StateFlow<R> = this.map {
        transform(it)
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = initialValue
    )
}