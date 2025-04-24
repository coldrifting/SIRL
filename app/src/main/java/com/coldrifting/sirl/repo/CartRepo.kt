package com.coldrifting.sirl.repo

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.objects.CartAisleEntry
import com.coldrifting.sirl.data.objects.CartAisleEntry.Companion.toHierarchy
import com.coldrifting.sirl.data.objects.RawCartEntry
import kotlinx.coroutines.flow.StateFlow

class CartRepo(
    val db: Database,
    val selectedStoreId: StateFlow<Int?>
) {
    fun getList(): List<CartAisleEntry> {
        val selectedStore = selectedStoreId.value
        if (selectedStore == null) {
            return emptyList()
        }

        // TODO - Add pop up if any aisles are missing
        val rawEntries = db.cartQueries.getShoppingList(selectedStore).executeAsList().map {
            RawCartEntry(
                it.aisleId ?: -1,
                it.aisleName ?: "No Aisle Set",
                it.itemId,
                it.itemName,
                it.prepName,
                it.unitType,
                it.totalAmount ?: 0
            )
        }
        return rawEntries.toHierarchy()
    }
}