package com.coldrifting.sirl.data.objects

data class RecipeTreeItemEntry(
    val recipeEntryId: Int,
    val itemId: Int,
    val itemName: String,
    val itemPrep: RecipeTreeItemPrep?,
    val amount: Amount
)