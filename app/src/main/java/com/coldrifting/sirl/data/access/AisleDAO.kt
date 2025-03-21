package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Aisle
import kotlinx.coroutines.flow.Flow

@Dao
interface AisleDAO: BaseDAO<Aisle> {
    @Query("SELECT * FROM Aisles ORDER BY sortingPrefix")
    fun all() : Flow<List<Aisle>>

    @Query("SELECT * FROM Aisles WHERE storeId = :storeId ORDER BY sortingPrefix")
    fun all(storeId: Int): Flow<List<Aisle>>

    @Query("UPDATE Aisles SET aisleName = :newAisleName WHERE aisleId = :aisleId")
    fun updateAisleName(aisleId: Int, newAisleName: String)


    @Query("SELECT MAX(sortingPrefix) FROM Aisles")
    fun maxSortingPrefixValue() : Int
}