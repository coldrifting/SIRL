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

    @Query("SELECT  " +
            "Recipes.recipeName " +
            "FROM Recipes " +
            "NATURAL JOIN RecipeSections " +
            "LEFT JOIN RecipeEntries ON RecipeEntries.recipeSectionId = RecipeSections.recipeSectionId " +
            "LEFT JOIN Items ON Items.itemId = RecipeEntries.itemId " +
            "LEFT JOIN ItemPreps ON RecipeEntries.itemPrepId = ItemPreps.itemPrepId " +
            "WHERE Items.itemId = :itemId " +
            "ORDER BY Recipes.recipeName")
    suspend fun getUsedItems(itemId: Int): List<String>

    @Query("SELECT  " +
            "Recipes.recipeName " +
            "FROM Recipes " +
            "NATURAL JOIN RecipeSections " +
            "LEFT JOIN RecipeEntries ON RecipeEntries.recipeSectionId = RecipeSections.recipeSectionId " +
            "LEFT JOIN Items ON Items.itemId = RecipeEntries.itemId " +
            "LEFT JOIN ItemPreps ON RecipeEntries.itemPrepId = ItemPreps.itemPrepId " +
            "WHERE ItemPreps.itemPrepId = :itemPrepId " +
            "ORDER BY Recipes.recipeName")
    suspend fun getUsedItemPreps(itemPrepId: Int): List<String>
}