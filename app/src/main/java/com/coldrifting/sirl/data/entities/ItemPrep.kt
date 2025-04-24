package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import kotlinx.serialization.Serializable

@Serializable
data class ItemPrep(
    val itemPrepId: Int,
    val itemId: Int,
    val prepName: String
): Insertable {
    override fun insert(database: Database) {
        database.itemsQueries.insertItemPrep(itemPrepId, itemId, prepName)
    }
}
