package com.coldrifting.sirl.repo

import android.content.Context
import com.coldrifting.sirl.data.access.AisleDAO
import com.coldrifting.sirl.data.access.StoreDAO
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Store
import com.coldrifting.sirl.util.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StoreRepository(
    val scope: CoroutineScope,
    val storeDao: StoreDAO,
    val aisleDao: AisleDAO,
    val context: Context,
    val selectedStoreId: StateFlow<Int>
) {

    val all = storeDao.all().toStateFlow(scope)

    private val allAisles = aisleDao.all().toStateFlow(scope)

    fun getAisles(storeId: Int): StateFlow<List<Aisle>> =
        aisleDao.all(storeId).toStateFlow(scope)

    // Store Selection
    fun select(storeId: Int) {
        scope.launch {
            storeDao.select(storeId)
        }
    }

    // Stores
    fun add(storeName: String) {
        scope.launch {
            storeDao.insert(Store(storeName = storeName))
        }
    }

    fun rename(storeId: Int, newName: String) {
        scope.launch {
            storeDao.insert(Store(storeId, newName))
        }
    }

    fun delete(storeId: Int) {
        if (all.value.size == 1) {
            return
        }

        scope.launch {
            // Ensure a store is always selected
            storeDao.select(all.value.filterNot{ s -> s.storeId == storeId}.first().storeId)

            storeDao.delete(Store(storeId = storeId, storeName = ""))
        }
    }

    fun getName(storeId: Int): String {
        return all.value.firstOrNull { s -> s.storeId == storeId }?.storeName ?: ""
    }

    // Aisles
    fun addAisle(storeId: Int, aisleName: String) {
        scope.launch {
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
        scope.launch {
            aisleDao.updateAisleName(aisleId, newAisleName)
        }
    }

    fun deleteAisle(aisleId: Int) {
        scope.launch {
            aisleDao.delete(Aisle(aisleId = aisleId, aisleName = "DELETE", storeId = -1))
        }
    }

    fun reorderAisles(items: List<Aisle>) {
        scope.launch {
            val list = getReorderedItems(items)
            aisleDao.insert(list)
        }
    }

    fun getAisleName(aisleId: Int): String {
        return allAisles.value.firstOrNull { a -> a.aisleId == aisleId }?.aisleName ?: ""
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
}