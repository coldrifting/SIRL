package com.coldrifting.sirl.data.access

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.coldrifting.sirl.data.access.base.BaseDAO
import com.coldrifting.sirl.data.helper.RawCartEntry
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.data.entities.RecipeEntry
import com.coldrifting.sirl.data.helper.RawRecipeEntry
import com.coldrifting.sirl.data.entities.RecipeSection
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

@Dao
interface RecipeDAO: BaseDAO<Recipe> {
    @Query("SELECT * FROM Recipes ORDER BY pinned DESC, LOWER(recipeName)")
    fun all(): Flow<List<Recipe>>

    @Query("UPDATE Recipes SET pinned = NOT pinned WHERE recipeId = :recipeId")
    fun togglePin(recipeId: Int)

    @Query("UPDATE RECIPES SET recipeName = :recipeName WHERE recipeId = :recipeId")
    fun setName(recipeId: Int, recipeName: String)

    @Query("UPDATE RECIPES SET recipeSteps = :recipeSteps WHERE recipeId = :recipeId")
    fun setSteps(recipeId: Int, recipeSteps: String)

    @Query("SELECT  " +
            "Recipes.recipeId, " +
            "Recipes.recipeName, " +
            "Recipes.recipeUrl, " +
            "Recipes.recipeSteps, " +
            "RecipeSections.recipeSectionId, " +
            "RecipeSections.sectionName, " +
            "RecipeSections.sortIndex, " +
            "RecipeEntries.recipeEntryId, " +
            "RecipeEntries.unitType, " +
            "RecipeEntries.amount, " +
            "Items.itemId, " +
            "Items.itemName, " +
            "ItemPreps.itemPrepId, " +
            "ItemPreps.prepName " +
            "FROM Recipes " +
            "NATURAL JOIN RecipeSections " +
            "LEFT JOIN RecipeEntries ON RecipeEntries.recipeSectionId = RecipeSections.recipeSectionId " +
            "LEFT JOIN Items ON Items.itemId = RecipeEntries.itemId " +
            "LEFT JOIN ItemPreps ON RecipeEntries.itemPrepId = ItemPreps.itemPrepId " +
            "WHERE Recipes.recipeId = :recipeId " +
            "ORDER BY RecipeSections.recipeSectionId ASC, RecipeEntries.recipeEntryId")
    fun getRecipes(recipeId: Int): Flow<List<RawRecipeEntry>>


    @Upsert
    fun insertSection(obj: RecipeSection)

    @Upsert
    fun insertSection(obj: List<RecipeSection>)

    fun populateSections(json: String) {
            val entries = Json.decodeFromString<List<RecipeSection>>(json)
            insertSection(entries)
    }

    @Query("UPDATE RecipeSections SET sectionName = :sectionName WHERE recipeSectionId = :recipeSectionId")
    fun renameSection(recipeSectionId: Int, sectionName: String)

    @Delete
    fun deleteSection(section: RecipeSection)

    @Upsert
    fun insertEntry(obj: RecipeEntry)

    @Upsert
    fun insertEntry(obj: List<RecipeEntry>)

    @Delete
    fun deleteEntry(obj: RecipeEntry)

    fun populateEntries(json: String) {
            val entries = Json.decodeFromString<List<RecipeEntry>>(json)
            insertEntry(entries)
    }

    @Query("UPDATE RecipeEntries SET amount = :amount WHERE RecipeEntries.recipeEntryId = :recipeItemEntryId")
    fun setRecipeItemEntryAmount(recipeItemEntryId: Int, amount: Float)

    @Query("UPDATE RecipeEntries SET unitType = :unitType WHERE RecipeEntries.recipeEntryId = :recipeItemEntryId")
    fun setRecipeItemEntryUnitType(recipeItemEntryId: Int, unitType: UnitType)

    @Query(
        "SELECT " +
        "Aisles.aisleId, " +
        "Aisles.aisleName, " +
        "Items.itemId, " +
        "Items.itemName, " +
        "ItemPreps.prepName, " +
        "RecipeEntries.unitType, " +
        "SUM(RecipeEntries.amount) as totalAmount " +
        "FROM Recipes " +
        "NATURAL JOIN RecipeSections " +
        "NATURAL JOIN RecipeEntries " +
        "NATURAL JOIN Items " +
        "LEFT JOIN ItemPreps ON RecipeEntries.itemPrepId = ItemPreps.itemPrepId " +
        "LEFT OUTER JOIN ItemAisles ON Items.itemId = ItemAisles.itemId AND ItemAisles.storeId = :storeId " +
        "LEFT OUTER JOIN Aisles ON Aisles.aisleId = ItemAisles.aisleId " +
        "WHERE Recipes.pinned = true " +
        "GROUP BY Items.itemName, ItemPreps.prepName " +
        "ORDER BY Aisles.sortingPrefix, " +
                "case when ItemAisles.bay = 'Start' then 1 " +
                    "when ItemAisles.bay = 'Middle' then 2 " +
                    "when ItemAisles.bay = 'End' then 3 end asc, " +
                "Items.itemName, " +
                "ItemPreps.prepName "
    )
    suspend fun getRawShoppingList(storeId: Int): List<RawCartEntry>
}