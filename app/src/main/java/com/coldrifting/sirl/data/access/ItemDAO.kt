package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO: BaseDAO<Item> {
    @Query("SELECT * FROM Items ORDER BY itemName")
    fun all(): Flow<List<Item>>

    @Query("SELECT * FROM Items  WHERE itemName LIKE '%' || :itemName || '%' ORDER BY itemName")
    fun all(itemName: String): Flow<List<Item>>
}