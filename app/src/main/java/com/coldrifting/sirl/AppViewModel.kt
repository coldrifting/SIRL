package com.coldrifting.sirl

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coldrifting.sirl.entities.StoreLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel: ViewModel() {
    private val repo: Repo = Repo()

    val selectedStore = repo.selectedStoreId

    val stores = repo.stores

    val aisles = repo.currentStoreLocations

    fun getStoreName(storeId: Int): String? {
        return repo.getStore(storeId)?.name
    }

    fun selectStore(storeId: Int) {
        repo.selectStore(storeId)
    }

    fun setCurrentStoreForEdit(storeId: Int) {
        repo.setCurrentStoreForEdit(storeId)
    }

    fun addStore(storeName: String) {
        repo.addStore(storeName)
    }

    fun deleteStore(storeId: Int) {
        repo.deleteStore(storeId)
    }

    fun renameStore(storeId: Int, newName: String) {
        repo.renameStore(storeId, newName)
    }

    fun syncAisles(items: List<StoreLocation>) {
        if (items != repo.currentStoreLocations.value) {
            repo.replaceStoreLocations(items)
        }
    }

    fun addAisle(storeId: Int, storeLocationName: String) {
        repo.addStoreLocation(storeId, storeLocationName)
    }

    fun renameAisle(storeId: Int, locationId: Int, newLocationName: String) {
        repo.renameStoreLocation(storeId, locationId, newLocationName)
    }

    fun deleteAisle(storeId: Int, index: Int) {
        repo.deleteStoreLocation(storeId, index)
    }

    private fun <T, R> StateFlow<T>.mapToStateFlow(
        transform: (T) -> R,
        initialValue: R
    ): StateFlow<R> = this.map {
        transform(it)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = initialValue
    )
}