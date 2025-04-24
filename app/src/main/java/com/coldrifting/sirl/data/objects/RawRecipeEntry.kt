package com.coldrifting.sirl.data.objects

import com.coldrifting.sirl.data.enums.UnitType

data class RawRecipeEntry(
    val recipeEntryId: Int = 0,
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String? = null,

    val recipeSectionId: Int = 0,
    val recipeSteps: String? = null,
    val sectionName: String = "",
    val sortIndex: Int = 0,

    val itemId: Int? = 0,
    val itemName: String? = "",
    val unitType: UnitType? = UnitType.EACHES,
    val amount: Int? = 0,

    val itemPrepId: Int? = null,
    val prepName: String? = null
) {
    companion object {
        fun toHierarchy(entries: List<RawRecipeEntry>): RecipeTree {
            if (entries.isEmpty()) {
                return RecipeTree()
            }

            val map: MutableMap<Int, RecipeTreeSection> = mutableMapOf()
            entries.forEach {
                val itemPrep : RecipeTreeItemPrep? = if (it.itemPrepId != null && it.prepName != null) {
                    RecipeTreeItemPrep(it.itemPrepId, it.prepName)
                } else null

                val item = if (it.itemId != null && it.itemName != null && it.unitType != null && it.amount != null) {
                    RecipeTreeItemEntry(it.recipeEntryId, it.itemId, it.itemName, itemPrep, it.unitType, it.amount)
                } else null

                // Get or create current section
                val section = map[it.recipeSectionId] ?: RecipeTreeSection(it.recipeSectionId, it.sectionName, it.sortIndex, listOf())

                // Update section in map
                val items = if (item == null) section.items else section.items + item
                map[section.sectionId] = RecipeTreeSection(it.recipeSectionId, it.sectionName, it.sortIndex, items)
            }

            val recipe = RecipeTree(
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


