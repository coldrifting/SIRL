package com.coldrifting.sirl.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSection(
    val recipeSectionId: Int,
    val recipeId: Int,
    val recipeSectionName: String,
    val sortIndex: Int
)
