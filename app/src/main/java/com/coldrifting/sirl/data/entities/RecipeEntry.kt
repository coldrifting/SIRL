package com.coldrifting.sirl.data.entities

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
)
