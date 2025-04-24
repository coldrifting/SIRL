package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.serialization.Serializable

@Serializable
data class RecipeEntry(
    val recipeEntryId: Int,
    val recipeId: Int,
    val recipeSectionId: Int,
    val itemId: Int,
    val itemPrepId: Int?,
    val unitType: UnitType,
    val amount: Int
): Insertable {
    override fun insert(database: Database) {
        database.recipesQueries.insertRecipeEntry(recipeEntryId, recipeId, recipeSectionId, itemId, itemPrepId, unitType, amount)
    }
}
