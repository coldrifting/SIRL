package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSection(
    val recipeSectionId: Int,
    val recipeId: Int,
    val recipeSectionName: String,
    val sortIndex: Int
) : Insertable {
    override fun insert(database: Database) {
        database.recipesQueries.insertRecipeSection(recipeSectionId, recipeId, recipeSectionName, sortIndex)
    }
}
