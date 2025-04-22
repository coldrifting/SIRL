package com.coldrifting.sirl.data.helper

import com.coldrifting.sirl.data.enums.UnitType

data class ItemWithPrepResult(
    val itemId: Int,
    val itemName: String,
    val defaultUnits: UnitType,
    val itemPrepId: Int?,
    val prepName: String?
) {
    fun toItemX(): RecipeTreeItem {
        return RecipeTreeItem(
            itemId = itemId,
            itemName = itemName,
            itemPrep =
                if (itemPrepId != null && prepName != null)
                    RecipeTreeItemPrep(itemPrepId, prepName)
                else
                    null,
            defaultUnits = defaultUnits)
    }
}