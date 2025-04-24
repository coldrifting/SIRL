package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import kotlinx.serialization.Serializable

@Serializable
data class Aisle(
    val aisleId: Int,
    val storeId: Int,
    val aisleName: String,
    val sortingPrefix: Int = 0
) : Insertable {
    override fun insert(database: Database) {
        database.storesQueries.insertAisle(aisleId, storeId, aisleName, sortingPrefix)
    }
}
