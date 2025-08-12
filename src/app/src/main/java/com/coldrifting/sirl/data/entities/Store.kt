package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val storeId: Int,
    val storeName: String,
    val selected: Boolean = false,
) : Insertable {
    override fun insert(database: Database) {
        database.storesQueries.insertStore(storeId, storeName, selected)
    }
}
