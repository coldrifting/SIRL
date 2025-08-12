package com.coldrifting.sirl.viewModel

import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.ItemAisle
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.repo.AppRepo
import com.coldrifting.sirl.data.enums.BayType
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.repo.ItemRepo
import kotlinx.coroutines.flow.StateFlow

class ItemViewModel(private val repository: AppRepo) {

    // UI Sort and Filter
    val sortingModeState: StateFlow<ItemRepo.ItemsSortingMode> =
        repository.items.itemsSortingModeState

    val filterTextState: StateFlow<String> =
        repository.items.itemsFilterTextState

    val filtered =
        repository.items.itemsWithFilter

    fun updateFilter(searchText: String) =
        repository.items.setFilterText(searchText)

    fun toggleItemSorting() =
        repository.items.toggleItemsSortingMode()

    // Items
    val allWithPrep = repository.items.allItemsWithPrep

    fun get(itemId: Int): StateFlow<Item> =
        repository.items.getItem(itemId)

    fun getDefaultItemType(itemId: Int) =
        repository.items.getDefaultItemType(itemId)

    fun add(itemName: String): Int =
        repository.items.addItem(itemName)

    fun rename(itemId: Int, itemName: String) =
        repository.items.setItemName(itemId, itemName)

    fun delete(itemId: Int) =
        repository.items.deleteItem(itemId)

    // Item Details
    fun setTemp(itemId: Int, itemTemp: ItemTemp) =
        repository.items.setItemTemp(itemId, itemTemp)

    fun setAisle(itemId: Int, aisleId: Int, bayType: BayType = BayType.Middle) =
        repository.items.updateItemAisle(itemId, aisleId, bayType)

    fun getAisle(itemId: Int): StateFlow<ItemAisle?> =
        repository.items.getItemAisle(itemId)

    fun setDefaultUnits(itemId: Int, unitType: UnitType) =
        repository.items.setItemDefaultUnits(itemId, unitType)

    // Item Preparations
    fun getPreps(itemId: Int): StateFlow<List<ItemPrep>> =
        repository.items.getItemPreparations(itemId)

    fun addPrep(itemId: Int, prepName: String) =
        repository.items.addItemPrep(itemId, prepName)

    fun renamePrep(prepId: Int, prepName: String) =
        repository.items.updateItemPrep(prepId, prepName)

    fun deletePrep(prepId: Int) =
        repository.items.deleteItemPrep(prepId)

    // Delete Precautions
    fun getUsedItems(itemId: Int): List<String> =
        repository.items.getUsedItems(itemId)

    fun getUsedItemPreps(itemPrepId: Int): List<String> =
        repository.items.getUsedItemPreps(itemPrepId)
}