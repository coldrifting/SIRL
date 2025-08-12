package com.coldrifting.sirl.data.objects

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
                val newEntry = CartAisleEntry(
                    entry.aisleId,
                    entry.aisleName,
                    aisleEntries + CartAisleItemEntry(entry.itemId, entry.itemName, entry.amount.toString())
                )
                entries[entry.aisleName] = newEntry
            }

            return entries.values.toList()
        }
    }
}