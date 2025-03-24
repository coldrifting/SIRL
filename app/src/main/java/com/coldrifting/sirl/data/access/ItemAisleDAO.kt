package com.coldrifting.sirl.data.access
import androidx.room.Dao
import androidx.room.Query
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.joined.ItemAisle
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemAisleDAO: BaseDAO<ItemAisle> {
    @Query("SELECT * FROM ItemAisles")
    fun all(): Flow<List<ItemAisle>>

    @Query("SELECT Items.itemId, Items.itemName, Items.itemTemp, Items.defaultUnits, x.storeId, x.aisleId, x.aisleName, x.sortingPrefix " +
            "From Items LEFT JOIN (SELECT * FROM ItemAisles NATURAL JOIN Aisles WHERE storeId = :storeId) as x " +
            "ON Items.itemId = x.itemId " +
            "WHERE Items.itemName LIKE '%' || :itemName || '%' " +
            "ORDER BY " +
            "CASE WHEN :sortingMode = 'Name' THEN LOWER(Items.itemName) END, " +
            "CASE WHEN :sortingMode = 'Temp' THEN Items.itemTemp END," +
            "x.sortingPrefix " )
    fun details(storeId: Int, sortingMode: String, itemName: String = ""): Flow<Map<Item, Aisle?>>

    @Query("SELECT * FROM ItemAisles WHERE itemId = :itemId AND storeId = :storeId")
    fun getByItem(itemId: Int, storeId: Int): Flow<ItemAisle?>
}