package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.data.enums.UnitType

data class RecipeEntryResult(
    val recipeEntryId: Int = 0,
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String = "",

    val recipeSectionId: Int = 0,
    val recipeSteps: String? = null,
    val sectionName: String = "",
    val sortIndex: Int = 0,

    val itemId: Int? = 0,
    val itemName: String? = "",
    val unitType: UnitType? = UnitType.EACHES,
    val amount: Float? = 0.0f,

    val itemPrepId: Int? = null,
    val prepName: String? = null
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

                val item = if (it.itemId != null && it.itemName != null && it.unitType != null && it.amount != null) {
                    RecipeItemEntryX(it.recipeEntryId, it.itemId, it.itemName, itemPrep, it.unitType, it.amount)
                } else null

                // Get or create current section
                val section = map[it.recipeSectionId] ?: RecipeSectionX(it.recipeSectionId, it.sectionName, it.sortIndex, listOf())

                // Update section in map
                val items = if (item == null) section.items else section.items + item
                map[section.sectionId] = RecipeSectionX(it.recipeSectionId, it.sectionName, it.sortIndex, items)
            }

            val recipe = RecipeX(
                recipeId = entries[0].recipeId,
                recipeName = entries[0].recipeName,
                recipeUrl = entries[0].recipeUrl,
                recipeSections = map.values.toList(),
                recipeSteps = entries[0].recipeSteps
            )
            return recipe
        }
    }
}

data class RecipeX(
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String = "",
    val recipeSections: List<RecipeSectionX> = listOf(),
    val recipeSteps: String? = null
)

data class RecipeSectionX(
    val sectionId: Int,
    val sectionName: String,
    val sortOrder: Int,
    val items: List<RecipeItemEntryX>
)

data class RecipeItemEntryX(
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