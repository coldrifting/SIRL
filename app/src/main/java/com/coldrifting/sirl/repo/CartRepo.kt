package com.coldrifting.sirl.repo

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.objects.CartAisleEntry.Companion.toHierarchy
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.data.objects.ChecklistItem
import com.coldrifting.sirl.data.objects.RawCartEntry
import com.coldrifting.sirl.repo.utils.toListStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class CartRepo(
    private val db: Database,
    scope: CoroutineScope,
    private val selectedStoreId: StateFlow<Int?>
) {
    val list = db.cartQueries.getCartList().toListStateFlow(scope) { list ->

        val map = mutableMapOf<Int, ChecklistHeader>()

        list.forEach { entry ->
            val header = map[entry.headerId]

            val newHeader = ChecklistHeader(
                id = entry.headerId,
                name = entry.headerName,
                expanded = entry.headerExpanded,
                items = (header?.items ?: listOf()) + ChecklistItem(
                    id = entry.cartItemId,
                    name = entry.cartItemName,
                    details = entry.cartItemDetails,
                    checked = entry.cartItemChecked))

            map[entry.headerId] = newHeader
        }

        map.values.toList()
    }

    fun toggleCartHeaderExpanded(cartHeaderId: Int) {
        val expanded = db.cartQueries.getCartHeaderExpanded(cartHeaderId).executeAsOne()
        db.cartQueries.toggleCartHeaderExpanded(!expanded, cartHeaderId)
    }

    fun toggleCartItemChecked(cartItemId: Int) {
        val checked = db.cartQueries.getCartItemChecked(cartItemId).executeAsOne()
        db.cartQueries.toggleCartItemChecked(!checked, cartItemId)
    }

    fun getItemsWithUnknownLocation(): List<String> {
        return db.cartQueries.getItemsWithUnknownLocation().executeAsList()
    }

    fun clearList() {
        db.cartQueries.clearCartHeaders()
        db.cartQueries.clearCartItems()
    }

    fun generateList() {
        val selectedStore = selectedStoreId.value
        if (selectedStore == null) {
            return
        }

        val rawEntries = db.cartQueries.getShoppingList(selectedStore).executeAsList()

        val entries = rawEntries.map{
            RawCartEntry(
                it.aisleId ?: -1,
                it.aisleName ?: "Unknown Location",
                it.itemId,
                it.itemName,
                it.prepName,
                it.unitType,
                it.totalAmount ?: 0
            )
        }.toHierarchy()

        db.cartQueries.transaction {
            db.cartQueries.clearCartHeaders()
            db.cartQueries.clearCartItems()
            entries.forEach { entry ->
                db.cartQueries.insertCartHeader(entry.aisleId, entry.aisleName)
                entry.entries.forEach { subEntry ->
                    db.cartQueries.insertCartItem(
                        subEntry.itemId,
                        entry.aisleId,
                        subEntry.itemName,
                        subEntry.amount
                    )
                }
            }
        }


    }
}