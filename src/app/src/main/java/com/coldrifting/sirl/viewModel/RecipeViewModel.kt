package com.coldrifting.sirl.viewModel

import com.coldrifting.sirl.data.objects.Amount
import com.coldrifting.sirl.data.objects.RecipeTree
import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.flow.StateFlow

class RecipeViewModel(private val repository: AppRepo) {
    val all = repository.recipes.all

    // Recipes
    fun add(recipeName: String): Int =
        repository.recipes.add(recipeName)

    fun delete(recipeId: Int) =
        repository.recipes.delete(recipeId)

    fun rename(recipeId: Int, recipeName: String) =
        repository.recipes.rename(recipeId, recipeName)

    fun pin(recipeId: Int) =
        repository.recipes.togglePin(recipeId)

    fun get(recipeId: Int): StateFlow<RecipeTree> =
        repository.recipes.getAllWithData(recipeId)

    // Recipe Sections
    fun renameSection(recipeSectionId: Int, sectionName: String) =
        repository.recipes.renameSection(recipeSectionId, sectionName)

    fun addSection(recipeId: Int, recipeSectionName: String) =
        repository.recipes.addSection(recipeId, recipeSectionName)

    fun deleteSection(recipeSectionId: Int) =
        repository.recipes.deleteSection(recipeSectionId)

    // Recipe (Section) Item Entries
    fun addItem(
        recipeSectionEntryId: Int?,
        recipeId: Int,
        recipeSectionId: Int,
        itemId: Int,
        itemPrepId: Int?,
        amount: Amount
    ) = repository.recipes.addEntry(
        recipeSectionEntryId,
        recipeId,
        recipeSectionId,
        itemId,
        itemPrepId,
        amount
    )

    fun deleteItem(recipeSectionEntryId: Int) =
        repository.recipes.deleteEntry(recipeSectionEntryId)

    fun setItemAmount(recipeItemEntryId: Int, amount: Amount) =
        repository.recipes.setEntryAmount(recipeItemEntryId, amount)

    fun editSteps(recipeId: Int, recipeSteps: String) =
        repository.recipes.setSteps(recipeId, recipeSteps)
}