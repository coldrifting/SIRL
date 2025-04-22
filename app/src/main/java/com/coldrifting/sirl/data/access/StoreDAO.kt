package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDAO: BaseDAO<Store> {
    @Query("SELECT * FROM Stores")
    fun all() : Flow<List<Store>>

    @Query("SELECT Stores.StoreId FROM Stores WHERE Stores.selected = true LIMIT 1")
    fun selected() : Flow<Int>

    @Query("UPDATE Stores SET selected = (Stores.storeId = :storeId)")
    fun select(storeId: Int)
}