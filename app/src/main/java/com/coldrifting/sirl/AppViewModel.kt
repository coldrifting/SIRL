package com.coldrifting.sirl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.Store
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    // StateFlows
    val stores: StateFlow<List<Store>> = repository.allStores
    fun getAislesAtStore(storeId: Int): StateFlow<List<Aisle>> {
        return repository.getAislesAtStore(storeId)
    }

    fun getItemWithFilter(itemName: String): StateFlow<List<Item>> {
        return repository.getItemsWithFilter(itemName)
    }

    // Store Selection
    val selectedStore = repository.selectedStoreId
    fun selectStore(storeId: Int) {
        repository.selectStore(storeId)
    }

    // Stores
    fun addStore(storeName: String) {
        repository.addStore(storeName)
    }

    fun deleteStore(storeId: Int) {
        repository.deleteStore(storeId)
    }

    fun renameStore(storeId: Int, newName: String) {
        repository.renameStore(storeId, newName)
    }

    fun getStoreName(storeId: Int): String {
        return repository.getStoreName(storeId)
    }

    // Aisles
    fun syncAisles(storeId: Int, items: List<Aisle>) {
        if (items != getAislesAtStore(storeId).value) {
            repository.reorderAisles(items)
        }
    }

    fun addAisle(storeId: Int, aisleName: String) {
        repository.addAisle(storeId, aisleName)
    }

    fun renameAisle(aisleId: Int, newAisleName: String) {
        repository.renameAisle(aisleId, newAisleName)
    }

    fun deleteAisle(aisleId: Int) {
        repository.deleteAisle(aisleId)
    }

    fun getAisleName(aisleId: Int): String {
        return repository.getAisleName(aisleId)
    }

    // Items
    fun addItem(itemName: String) {
        repository.addItem(itemName)
    }

    fun deleteItem(itemId: Int) {
        repository.deleteItem(itemId)
    }

    fun getItemName(itemId: Int): String {
        return repository.getItemName(itemId)
    }

    fun trySelectStore() {
        repository.trySelectStore()
    }

    companion object AppViewModelProvider {
        // Fetches the application singleton and extracts the repository in it
        val Factory = viewModelFactory {
            val appKey = ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
            initializer {
                AppViewModel(
                    (this[appKey] as AppApplication).appRepository
                )
            }
        }
    }
}
