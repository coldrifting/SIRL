package com.coldrifting.sirl.db

import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.coldrifting.sirl.AislesQueries
import com.coldrifting.sirl.StoresQueries
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.util.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class StoresRepo(
    val scope: CoroutineScope,
    private val storesQueries: StoresQueries,
    private val aislesQueries: AislesQueries,
    val selectedStoreId: StateFlow<Int>
) {
    val all = storesQueries.getAll().asFlow().mapToList(scope.coroutineContext).toStateFlow(scope)

    fun add(name: String) {
        storesQueries.add(name)
    }

    fun rename(storeId: Int, name: String) {
        storesQueries.rename(name, storeId)
    }

    fun delete(storeId: Int) {
        storesQueries.delete(storeId)
    }

    fun getName(storeId: Int): String {
        return all.value.firstOrNull { s -> s.storeId == storeId }?.storeName ?: ""
    }

    fun select(storeId: Int) {
        storesQueries.select(storeId)
    }

    fun selected(): Flow<Int> {
        return storesQueries.selected()
            .asFlow()
            .mapToOne(scope.coroutineContext)
            .map { s -> s.toInt() }
            .toStateFlow(scope, -1)
    }

    // Aisles
    private val allAisles = aislesQueries.getAll()
        .asFlow()
        .mapToList(scope.coroutineContext)
        .toStateFlow(scope)

    fun getAisles(storeId: Int) = aislesQueries.getAllFromStore(storeId)
        .asFlow()
        .mapToList(scope.coroutineContext)
        .toStateFlow(scope)

    fun addAisle(storeId: Int, aisleName: String) {
        aislesQueries.transaction {
            val max = (aislesQueries.getMaxSort().executeAsOne().expr?.toInt() ?: 0) + 1
            aislesQueries.add(storeId, aisleName, max)
        }
    }

    fun renameAisle(aisleId: Int, newAisleName: String) {
        aislesQueries.rename(newAisleName, aisleId)
    }

    fun deleteAisle(aisleId: Int) {
        aislesQueries.delete(aisleId)
    }

    fun reorderAisles(items: List<Aisle>) {
        val list = getReorderedItems(items)
        Log.d("TEST", "ReORDER")
        aislesQueries.transaction {
            list.forEach { aisle ->
                aislesQueries.updateSort(aisle.sortingPrefix, aisle.aisleId)
            }
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