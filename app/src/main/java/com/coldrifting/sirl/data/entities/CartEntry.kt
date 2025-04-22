package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.enums.getPrepAbbreviation

data class RawCartEntry(
    val aisleId: Int,
    val aisleName: String,
    val itemId: Int,
    val itemName: String,
    val prepName: String?,
    val unitType: UnitType,
    val totalAmount: Float
)

fun List<RawCartEntry>.toHierarchy(): List<CartAisleEntry> {
    var entries = mutableMapOf<String, CartAisleEntry>()

    this.forEach {  entry ->
        var aisleEntries = entries[entry.aisleName]?.entries ?: emptyList()
        // TODO - Convert to ounces if relevant (Cups, Tbsp, Tsp)
        val amount = entry.unitType.getPrepAbbreviation(entry.totalAmount)
        val newEntry = CartAisleEntry(
            entry.aisleId,
            entry.aisleName,
            aisleEntries + CartAisleItemEntry(entry.itemId, entry.itemName, amount))
        entries[entry.aisleName] = newEntry
    }

    return entries.values.toList()
}

data class CartAisleEntry(
    val aisleId: Int,
    val aisleName: String,
    val entries: List<CartAisleItemEntry>
)

data class CartAisleItemEntry(
    val itemId: Int,
    val itemName: String,
    val amount: String
)