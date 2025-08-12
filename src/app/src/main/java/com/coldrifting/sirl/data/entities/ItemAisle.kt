package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import com.coldrifting.sirl.data.enums.BayType
import kotlinx.serialization.Serializable

@Serializable
data class ItemAisle(
    val itemId: Int,
    val storeId: Int,
    val aisleId: Int,
    val bay: BayType = BayType.Middle
) : Insertable {
    override fun insert(database: Database) {
        database.itemsQueries.insertItemAisle(itemId, storeId, aisleId, bay)
    }
}
