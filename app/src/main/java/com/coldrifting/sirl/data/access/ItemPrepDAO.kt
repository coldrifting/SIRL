package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.ItemPrep
import com.coldrifting.sirl.data.entities.ItemWithPrepResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemPrepDAO: BaseDAO<ItemPrep> {
    @Query("SELECT * FROM ItemPreps")
    fun all(): Flow<List<ItemPrep>>

    @Query("UPDATE ItemPreps SET prepName = :prepName WHERE itemPrepId = :prepId")
    fun update(prepId: Int, prepName: String)

    @Query("SELECT * FROM ItemPreps WHERE itemId = :itemId")
    fun getItemPreps(itemId: Int): Flow<List<ItemPrep>>

    @Query("SELECT * FROM Items LEFT JOIN ItemPreps ON Items.itemId = ItemPreps.itemId ORDER BY Items.itemName, ItemPreps.prepName")
    fun getAllItemsAndPreps(): Flow<List<ItemWithPrepResult>>
}