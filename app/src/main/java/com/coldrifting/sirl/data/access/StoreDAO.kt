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

    @Query("SELECT COALESCE((SELECT MIN(storeId) FROM Stores), -1)")
    fun firstStoreIdOrDefault() : Int
}