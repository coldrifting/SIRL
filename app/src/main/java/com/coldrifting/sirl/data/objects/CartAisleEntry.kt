package com.coldrifting.sirl.data.objects

import com.coldrifting.sirl.data.enums.getPrepAbbreviation

data class CartAisleEntry(
    val aisleId: Int,
    val aisleName: String,
    val entries: List<CartAisleItemEntry>
) {
    companion object {
        fun List<RawCartEntry>.toHierarchy(): List<CartAisleEntry> {
            var entries = mutableMapOf<String, CartAisleEntry>()

            this.forEach { entry ->
                var aisleEntries = entries[entry.aisleName]?.entries ?: emptyList()
                val amount = entry.unitType.getPrepAbbreviation(entry.totalAmount)
                val newEntry = CartAisleEntry(
                    entry.aisleId,
                    entry.aisleName,
                    aisleEntries + CartAisleItemEntry(entry.itemId, entry.itemName, amount)
                )
                entries[entry.aisleName] = newEntry
            }

            return entries.values.toList()
        }
    }
}