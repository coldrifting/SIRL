package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import com.coldrifting.sirl.data.objects.Amount
import kotlinx.serialization.Serializable

@Serializable
data class RecipeEntry(
    val recipeEntryId: Int,
    val recipeId: Int,
    val recipeSectionId: Int,
    val itemId: Int,
    val itemPrepId: Int?,
    val amount: Amount
): Insertable {
    override fun insert(database: Database) {
        database.recipesQueries.insertRecipeEntry(recipeEntryId, recipeId, recipeSectionId, itemId, itemPrepId, amount)
    }
}
