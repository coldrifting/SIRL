package com.coldrifting.sirl.view

import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class StoreViewModel(private val repository: AppRepo) {

    val all = repository.store.all

    // Store Selection
    val selected = repository.store.selectedStoreId

    fun select(storeId: Int) =
        repository.store.select(storeId)

    // Stores
    fun add(storeName: String, select: Boolean) =
        repository.store.add(storeName, select)

    fun delete(storeId: Int) =
        repository.store.delete(storeId)

    fun rename(storeId: Int, newName: String) =
        repository.store.rename(storeId, newName)

    fun getName(storeId: Int): String =
        repository.store.getName(storeId)

    // Aisles
    fun addAisle(storeId: Int, aisleName: String) =
        repository.store.addAisle(storeId, aisleName)

    fun renameAisle(aisleId: Int, newAisleName: String) =
        repository.store.renameAisle(aisleId, newAisleName)

    fun deleteAisle(aisleId: Int) =
        repository.store.deleteAisle(aisleId)

    fun getAisleName(aisleId: Int): String =
        repository.store.getAisleName(aisleId)

    fun getAisles(storeId: Int) =
        repository.store.getAisles(storeId)

    val firstItemIndexState = MutableStateFlow(0)
    fun syncAisles(storeId: Int, items: List<Aisle>, firstItemIndex: Int) {
        if (items != getAisles(storeId).value) {
            repository.store.reorderAisles(items)
            firstItemIndexState.update {
                firstItemIndex
            }
        }
    }
}