package com.coldrifting.sirl.data.access
import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.joined.ItemAisle
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemAisleDAO: BaseDAO<ItemAisle> {
    @Query("SELECT * FROM ItemAisles")
    fun all(): Flow<List<ItemAisle>>
}