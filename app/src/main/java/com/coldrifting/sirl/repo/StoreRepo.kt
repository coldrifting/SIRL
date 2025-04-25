package com.coldrifting.sirl.repo

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.repo.utils.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StoreRepo(
    private val db: Database,
    private val scope: CoroutineScope,
    val selectedStoreId: StateFlow<Int>
) {
fun <T> Flow<T>.toStateFlow(scope: CoroutineScope, initialValue: T): StateFlow<T> =
    this.stateIn(scope, SharingStarted.Eagerly, initialValue)

fun <T> Flow<List<T>>.toStateFlow(scope: CoroutineScope): StateFlow<List<T>> =
    this.toStateFlow(scope, listOf())

fun <T: Any> Query<T>.toListStateFlow(scope: CoroutineScope): StateFlow<List<T>> =
    this.asFlow().mapToList(scope.coroutineContext).toStateFlow(scope)

    val all = db.storesQueries.allStores().toListStateFlow(scope)

    fun add(name: String, select: Boolean): Int {
        db.storesQueries.addStore(name, select)
        val storeId = db.storesQueries.lastInsertRowId().executeAsOne().toInt()
        return storeId
    }

    fun rename(storeId: Int, name: String) {
        db.storesQueries.renameStore(name, storeId)
    }

    fun delete(storeId: Int) {
        db.storesQueries.deleteStore(storeId)
    }

    fun getName(storeId: Int): String {
        return all.value.firstOrNull { s -> s.storeId == storeId }?.storeName ?: ""
    }

    fun select(storeId: Int) {
        all.value.forEach {
            if (it.selected && it.storeId != storeId) {
                db.storesQueries.transaction {
                    db.storesQueries.selectStore(false, it.storeId)
                    db.storesQueries.selectStore(true, storeId)
                }
            }
            return@forEach
        }
    }

    fun selected(): Flow<Int> {
        return db.storesQueries.selectedStore().toStateFlow(scope, -1) { selectedStore ->
            selectedStore.toInt()
        }
    }

    // Aisles
    private val allAisles = db.storesQueries.allAisles().toListStateFlow(scope)

    fun getAisles(storeId: Int) = db.storesQueries.allAislesFromStore(storeId).toListStateFlow(scope)

    fun addAisle(storeId: Int, aisleName: String) {
        db.storesQueries.transaction {
            val max = (db.storesQueries.aisleMaxSortIndex().executeAsOne().expr?.toInt() ?: 0) + 1
            db.storesQueries.addAisle(storeId, aisleName, max)
        }
    }

    fun renameAisle(aisleId: Int, newAisleName: String) {
        db.storesQueries.renameAisle(newAisleName, aisleId)
    }

    fun deleteAisle(aisleId: Int) {
        db.storesQueries.deleteAisle(aisleId)
    }

    fun reorderAisles(items: List<Aisle>) {
        val list = getReorderedItems(items)
        db.storesQueries.transaction {
            list.forEach { aisle ->
                db.storesQueries.updateAisleSort(aisle.sortingPrefix, aisle.aisleId)
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