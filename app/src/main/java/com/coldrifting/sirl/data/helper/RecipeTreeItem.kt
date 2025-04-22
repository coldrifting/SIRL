package com.coldrifting.sirl.data.helper

import com.coldrifting.sirl.data.enums.UnitType

data class RecipeTreeItem(
    val itemId: Int,
    val itemName: String,
    val itemPrep: RecipeTreeItemPrep?,
    val defaultUnits: UnitType
) {
    override fun toString(): String {
        return itemName + if (itemPrep != null) " - ${itemPrep.prepName}" else ""
    }

    fun equals(recipeItemEntry: RecipeTreeItemEntry): Boolean {
        return this.itemId == recipeItemEntry.itemId && this.itemPrep == recipeItemEntry.itemPrep
    }
}