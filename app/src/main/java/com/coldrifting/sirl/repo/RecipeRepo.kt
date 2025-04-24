package com.coldrifting.sirl.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.enums.UnitType
import com.coldrifting.sirl.data.objects.RawRecipeEntry
import com.coldrifting.sirl.data.objects.RecipeTree
import com.coldrifting.sirl.repo.utils.toListStateFlow
import com.coldrifting.sirl.repo.utils.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class RecipeRepo(
    val scope: CoroutineScope,
    val db: Database
) {
    val all = db.recipesQueries.getAll().toListStateFlow(scope)

    fun togglePin(recipeId: Int) {
        db.recipesQueries.togglePinned(recipeId)
    }

    fun add(recipeName: String) {
        db.recipesQueries.add(recipeName)
        val recipeId = db.recipesQueries.lastInsertRowId().executeAsOne().toInt()
        db.recipeSectionsQueries.add(recipeId, "Main")
    }

    fun rename(recipeId: Int, recipeName: String) {
        db.recipesQueries.rename(recipeName, recipeId)
    }

    fun delete(recipeId: Int) {
        db.recipesQueries.delete(recipeId)
    }

    fun setSteps(recipeId: Int, recipeSteps: String) {
        db.recipesQueries.setSteps(recipeSteps, recipeId)
    }

    fun getAllWithData(recipeId: Int): StateFlow<RecipeTree> {
        return db.recipesQueries.getDetails(recipeId)
            .asFlow()
            .mapToList(scope.coroutineContext)
            .map{ RawRecipeEntry.toHierarchy(it.map {  RawRecipeEntry(
                recipeEntryId = it.recipeEntryId ?: 0,
                recipeId = it.recipeId,
                recipeName = it.recipeName,
                recipeUrl = it.url,
                recipeSectionId = it.recipeSectionId,
                recipeSteps = it.steps,
                sectionName = it.recipeSectionName,
                sortIndex = it.sortIndex,
                itemId = it.itemId,
                itemName = it.itemName,
                unitType = it.unitType,
                amount = it.amount,
                itemPrepId = it.itemPrepId,
                prepName = it.prepName
            ) }) }
            .toStateFlow(scope, RecipeTree())
    }

    fun setEntryAmount(recipeItemEntryId: Int, unitType: UnitType, amount: Int) {
        db.recipeEntriesQueries.updateAmountAndType(amount, unitType, recipeItemEntryId)
    }

    fun addEntry(recipeSectionEntryId: Int?, recipeId: Int, recipeSectionId: Int, itemId: Int, itemPrepId: Int?, unitType: UnitType, amount: Int) {
        if (recipeSectionEntryId != null) {
            db.recipeEntriesQueries.insert(
                recipeSectionEntryId,
                recipeId,
                recipeSectionId,
                itemId,
                itemPrepId,
                unitType,
                amount)
        }
        else {
            db.recipeEntriesQueries.add(
                recipeId,
                recipeSectionId,
                itemId,
                itemPrepId,
                unitType,
                amount)
        }
    }

    fun deleteEntry(recipeSectionEntryId: Int) {
        db.recipeEntriesQueries.delete(recipeSectionEntryId)
    }

    fun addSection(recipeId: Int, recipeSectionName: String) {
        db.recipeSectionsQueries.add(recipeId, recipeSectionName)
    }

    fun renameSection(recipeSectionId: Int, recipeSectionName: String) {
        db.recipeSectionsQueries.rename(recipeSectionName, recipeSectionId)
    }

    fun deleteSection(recipeSectionId: Int) {
        db.recipeSectionsQueries.delete(recipeSectionId)
    }
}