package com.coldrifting.sirl.data.helper

import com.coldrifting.sirl.data.enums.UnitType

data class RecipeTreeItemEntry(
    val recipeEntryId: Int,
    val itemId: Int,
    val itemName: String,
    val itemPrep: RecipeTreeItemPrep?,
    val unitType: UnitType,
    val amount: Float
)