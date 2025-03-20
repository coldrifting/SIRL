package com.coldrifting.sirl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    val stores: StateFlow<List<Store>> = repository.allStores
    val locations: StateFlow<List<StoreLocation>> = repository.allLocations

    val selectedStore = repository.selectedStoreId

    fun selectStore(storeId: Int) {
        repository.selectStore(storeId)
    }

    fun setCurrentStoreForEdit(storeId: Int) {
        repository.setCurrentStoreForEdit(storeId)
    }


    fun addStore(storeName: String) {
        repository.addStore(storeName)
    }

    fun deleteStore(storeId: Int) {
        repository.deleteStore(storeId)
    }

    fun renameStore(storeId: Int, newName: String) {
        repository.renameStore(storeId, newName)
    }

    fun getStoreName(storeId: Int): String? {
        return stores.value.firstOrNull{ s -> s.storeId == storeId}?.storeName
    }


    fun syncAisles(items: List<StoreLocation>) {
        if (items != locations.value) {
            repository.reorderStoreLocations(items)
        }
    }

    fun addAisle(storeId: Int, storeLocationName: String) {
        repository.addStoreLocation(storeId, storeLocationName)
    }

    fun renameAisle(locationId: Int, newLocationName: String) {
        repository.renameStoreLocation(locationId, newLocationName)

    }

    fun deleteAisle(locationId: Int) {
        repository.deleteStoreLocation(locationId)
    }

    fun getAisleName(locationId: Int): String? {
        return locations.value.firstOrNull{ s -> s.locationId == locationId}?.locationName
    }
}

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppViewModel(
                //fetches the application singleton
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        //and then extracts the repository in it
                        as AppApplication).appRepository
            )
        }
    }
}