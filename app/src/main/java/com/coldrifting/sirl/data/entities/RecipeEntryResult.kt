package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.data.enums.UnitType

data class RecipeEntryResult(
    val recipeEntryId: Int = 0,
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String = "",
    val recipeSectionId: Int = 0,
    val sectionName: String = "",
    val sortIndex: Int = 0,
    val itemId: Int = 0,
    val itemName: String = "",
    val itemPrepId: Int? = null,
    val prepName: String? = null,
    val unitType: UnitType = UnitType.EACHES,
    val amount: Float = 0.0f
) {
    companion object {
        fun toHierarchy(entries: List<RecipeEntryResult>): RecipeX {
            if (entries.isEmpty()) {
                return RecipeX()
            }

            val map: MutableMap<Int, RecipeSectionX> = mutableMapOf()
            entries.forEach {
                val itemPrep : ItemPrepX? = if (it.itemPrepId != null && it.prepName != null) {
                    ItemPrepX(it.itemPrepId, it.prepName)
                } else null

                val item = ItemsX(it.recipeEntryId, it.itemId, it.itemName, itemPrep, it.unitType, it.amount)

                val section = map[it.recipeSectionId] ?: RecipeSectionX(it.recipeSectionId, it.sectionName, it.sortIndex, listOf())
                map[section.sectionId] = RecipeSectionX(it.recipeSectionId, it.sectionName, it.sortIndex, section.items + item)
            }

            val recipe = RecipeX(entries[0].recipeId, entries[0].recipeName, entries[0].recipeUrl, map.values.toList())
            return recipe
        }
    }
}

data class RecipeX(
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String = "",
    val recipeSections: List<RecipeSectionX> = listOf()
)

data class RecipeSectionX(
    val sectionId: Int,
    val sectionName: String,
    val sortOrder: Int,
    val items: List<ItemsX>
)

data class ItemsX(
    val recipeEntryId: Int,
    val itemId: Int,
    val itemName: String,
    val itemPrep: ItemPrepX?,
    val unitType: UnitType,
    val amount: Float
)

data class ItemPrepX(
    val itemPrepId: Int,
    val prepName: String
)