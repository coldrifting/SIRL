package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.entities.interfaces.Insertable
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val recipeId: Int,
    val recipeName: String,
    val url: String? = null,
    val pinned: Boolean = false,
    val steps: String? = null
): Insertable {
    override fun insert(database: Database) {
        database.recipesQueries.insertRecipe(recipeId, recipeName, url, pinned, steps)
    }
}
