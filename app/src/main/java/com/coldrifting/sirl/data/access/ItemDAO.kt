package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.enums.ItemTemp
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO: BaseDAO<Item> {
    @Query("SELECT * FROM Items ORDER BY itemName")
    fun all(): Flow<List<Item>>

    @Query("SELECT * FROM Items WHERE itemName LIKE '%' || :itemName || '%' ORDER BY itemName")
    fun all(itemName: String): Flow<List<Item>>

    @Query("SELECT * FROM Items WHERE itemId = :itemId")
    fun getItem(itemId: Int): Flow<Item>

    @Query("UPDATE Items SET itemName = :itemName WHERE itemId = :itemId")
    fun updateName(itemId: Int, itemName: String)

    @Query("UPDATE Items SET itemTemp = :itemTemp WHERE itemId = :itemId")
    fun updateTemp(itemId: Int, itemTemp: ItemTemp)

    @Query("UPDATE Items SET defaultUnits = :defaultUnits WHERE itemId = :itemId")
    fun updateItemDefaultUnits(itemId: Int, defaultUnits: UnitType)
}