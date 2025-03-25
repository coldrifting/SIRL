package com.coldrifting.sirl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.coldrifting.sirl.data.AppApplication
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.data.entities.joined.ItemAisle
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    // StateFlows
    val stores: StateFlow<List<Store>> = repository.allStores
    fun getAislesAtStore(storeId: Int): StateFlow<List<Aisle>> {
        return repository.getAislesAtStore(storeId)
    }

    val allRecipes = repository.allRecipes

    val itemsSortingModeState: StateFlow<AppRepository.ItemsSortingMode> = repository.itemsSortingModeState
    val itemsFilterTextState: StateFlow<String> = repository.itemsFilterTextState
    val itemsWithFilter = repository.itemsWithFilter

    val firstItemIndexState = MutableStateFlow(0)

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
    fun syncAisles(storeId: Int, items: List<Aisle>, firstItemIndex: Int) {
        if (items != getAislesAtStore(storeId).value) {
            repository.reorderAisles(items)
            firstItemIndexState.update {
                firstItemIndex
            }
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

    fun getItem(itemId: Int): StateFlow<Item> {
        return repository.getItem(itemId)
    }

    fun setItemTemp(itemId: Int, itemTemp: ItemTemp) {
        repository.setItemTemp(itemId, itemTemp)
    }

    fun setItemName(itemId: Int, itemName: String) {
        repository.setItemName(itemId, itemName)
    }

    fun trySelectStore() {
        repository.trySelectStore()
    }

    fun toggleItemSorting() {
        repository.toggleItemsSortingMode()
    }

    fun updateItemFilter(searchText: String) {
        repository.setFilterText(searchText)
    }

    fun updateItemAisle(itemId: Int, aisleId: Int, bayType: BayType = BayType.Middle) {
        repository.updateItemAisle(itemId, aisleId, bayType)
    }

    fun getItemAisle(itemId: Int): StateFlow<ItemAisle?> {
        return repository.getItemAisle(itemId)
    }

    fun setItemDefaultUnits(itemId: Int, unitType: UnitType) {
        repository.setItemDefaultUnits(itemId, unitType)
    }

    fun getItemPreparations(itemId: Int): StateFlow<List<ItemPrep>> {
        return repository.getItemPreparations(itemId)
    }

    fun addItemPrep(itemId: Int, prepName: String) {
        repository.addItemPrep(itemId, prepName)
    }

    fun updateItemPrep(prepId: Int, prepName: String) {
        repository.updateItemPrep(prepId, prepName)
    }

    fun deleteItemPrep(prepId: Int) {
        repository.deleteItemPrep(prepId)
    }

    fun toggleRecipePin(recipeId: Int) {
        repository.toggleRecipePin(recipeId)
    }

    fun addRecipe(recipeName: String) {
        repository.addRecipe(recipeName)
    }

    fun deleteRecipe(recipeId: Int) {
        repository.deleteRecipe(recipeId)
    }

    fun setRecipeName(recipeId: Int, recipeName: String) {
        repository.setRecipeName(recipeId, recipeName)
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
