package com.coldrifting.sirl.repo

import com.coldrifting.sirl.data.access.RecipeDAO
import com.coldrifting.sirl.data.entities.Recipe
import com.coldrifting.sirl.data.entities.RecipeEntry
import com.coldrifting.sirl.data.entities.RecipeSection
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.helper.RawRecipeEntry
import com.coldrifting.sirl.data.helper.RecipeTree
import com.coldrifting.sirl.util.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class RecipeRepository(
    private val recipeDao: RecipeDAO,
    private val scope: CoroutineScope
) {
    val all = recipeDao.all().toStateFlow(scope)

    fun togglePin(recipeId: Int) {
        scope.launch {
            recipeDao.togglePin(recipeId)
        }
    }

    fun add(recipeName: String) {
        val recipe = Recipe(recipeName = recipeName)
        scope.launch {
            val id = recipeDao.insert(recipe)
            recipeDao.insertSection(RecipeSection(recipeId = id.toInt(), sectionName = "Main"))
        }
    }

    fun rename(recipeId: Int, recipeName: String) {
        scope.launch {
            recipeDao.setName(recipeId, recipeName)
        }
    }

    fun delete(recipeId: Int) {
        val recipe = Recipe(recipeId = recipeId, recipeName = "DELETE")
        scope.launch {
            recipeDao.delete(recipe)
        }
    }

    fun setSteps(recipeId: Int, recipeSteps: String) {
        scope.launch {
            recipeDao.setSteps(recipeId, recipeSteps)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllWithData(recipeId: Int): StateFlow<RecipeTree> {
        return recipeDao.getRecipes(recipeId).transformLatest { x ->
            emit(RawRecipeEntry.Companion.toHierarchy(x))
        }.toStateFlow(scope, RecipeTree())
    }

    fun setEntryAmount(recipeItemEntryId: Int, unitType: UnitType, amount: Float) {
        scope.launch {
            recipeDao.setRecipeItemEntryAmount(recipeItemEntryId, amount)
            recipeDao.setRecipeItemEntryUnitType(recipeItemEntryId, unitType)
        }
    }

    fun addEntry(recipeSectionEntryId: Int?, recipeId: Int, recipeSectionId: Int, itemId: Int, itemPrepId: Int?, unitType: UnitType, amount: Float) {
        scope.launch {
            if (recipeSectionEntryId != null) {
                recipeDao.insertEntry(
                    RecipeEntry(
                        recipeEntryId = recipeSectionEntryId,
                        recipeId = recipeId,
                        recipeSectionId = recipeSectionId,
                        itemId = itemId,
                        itemPrepId = itemPrepId,
                        unitType = unitType,
                        amount = amount
                    )
                )
            }
            else {
                recipeDao.insertEntry(
                    RecipeEntry(
                        recipeId = recipeId,
                        recipeSectionId = recipeSectionId,
                        itemId = itemId,
                        itemPrepId = itemPrepId,
                        unitType = unitType,
                        amount = amount
                    )
                )
            }
        }
    }

    fun deleteEntry(recipeSectionEntryId: Int) {
        scope.launch {
            recipeDao.deleteEntry(RecipeEntry(recipeEntryId = recipeSectionEntryId))
        }
    }

    fun addSection(recipeId: Int, recipeSectionName: String) {
        scope.launch {
            recipeDao.insertSection(
                RecipeSection(
                    recipeId = recipeId,
                    sectionName = recipeSectionName
                )
            )
        }
    }

    fun renameSection(recipeSectionId: Int, recipeSectionName: String) {
        scope.launch {
            recipeDao.renameSection(recipeSectionId, recipeSectionName)
        }
    }

    fun deleteSection(recipeSectionId: Int) {
        scope.launch {
            recipeDao.deleteSection(
                RecipeSection(
                    recipeSectionId = recipeSectionId,
                    recipeId = -1,
                    sectionName = ""
                )
            )
        }
    }
}