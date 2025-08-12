package com.coldrifting.sirl.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.objects.Amount
import com.coldrifting.sirl.data.objects.RawRecipeEntry
import com.coldrifting.sirl.data.objects.RecipeTree
import com.coldrifting.sirl.repo.utils.toListStateFlow
import com.coldrifting.sirl.repo.utils.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class RecipeRepo(
    private val scope: CoroutineScope,
    private val db: Database
) {
    val all = db.recipesQueries.allRecipes().toListStateFlow(scope)

    fun togglePin(recipeId: Int) {
        db.recipesQueries.toggleRecipePin(recipeId)
    }

    fun add(recipeName: String): Int {
        db.recipesQueries.addRecipe(recipeName)
        val recipeId = db.recipesQueries.lastInsertRowId().executeAsOne().toInt()
        db.recipesQueries.addRecipeSection(recipeId, "Main")
        return recipeId
    }

    fun rename(recipeId: Int, recipeName: String) {
        db.recipesQueries.renameRecipe(recipeName, recipeId)
    }

    fun delete(recipeId: Int) {
        db.recipesQueries.deleteRecipe(recipeId)
    }

    fun setSteps(recipeId: Int, recipeSteps: String) {
        db.recipesQueries.setRecipeSteps(recipeSteps, recipeId)
    }

    fun getAllWithData(recipeId: Int): StateFlow<RecipeTree> {
        return db.recipesQueries.recipeDetails(recipeId)
            .asFlow()
            .mapToList(scope.coroutineContext)
            .map{ RawRecipeEntry.toTree(it.map {  RawRecipeEntry(
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
                amount = it.amount,
                itemPrepId = it.itemPrepId,
                prepName = it.prepName
            ) }) }
            .toStateFlow(scope, RecipeTree())
    }


    fun addSection(recipeId: Int, recipeSectionName: String) {
        db.recipesQueries.addRecipeSection(recipeId, recipeSectionName)
    }

    fun renameSection(recipeSectionId: Int, recipeSectionName: String) {
        db.recipesQueries.renameRecipeSection(recipeSectionName, recipeSectionId)
    }

    fun deleteSection(recipeSectionId: Int) {
        db.recipesQueries.deleteRecipeSection(recipeSectionId)
    }


    fun setEntryAmount(recipeItemEntryId: Int, amount: Amount) {
        db.recipesQueries.updateRecipeEntryUnits(amount, recipeItemEntryId)
    }

    fun addEntry(recipeSectionEntryId: Int?, recipeId: Int, recipeSectionId: Int, itemId: Int, itemPrepId: Int?, amount: Amount) {
        if (recipeSectionEntryId != null) {
            db.recipesQueries.insertRecipeEntry(
                recipeSectionEntryId,
                recipeId,
                recipeSectionId,
                itemId,
                itemPrepId,
                amount)
        }
        else {
            db.recipesQueries.addRecipeEntry(
                recipeId,
                recipeSectionId,
                itemId,
                itemPrepId,
                amount)
        }
    }

    fun deleteEntry(recipeSectionEntryId: Int) {
        db.recipesQueries.deleteRecipeEntry(recipeSectionEntryId)
    }
}