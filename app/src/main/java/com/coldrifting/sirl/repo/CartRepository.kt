package com.coldrifting.sirl.repo

import com.coldrifting.sirl.data.access.RecipeDAO
import com.coldrifting.sirl.data.helper.CartAisleEntry
import com.coldrifting.sirl.data.helper.CartAisleEntry.Companion.toHierarchy
import kotlinx.coroutines.flow.StateFlow

class CartRepository(
    val recipeDao: RecipeDAO,
    val selectedStoreId: StateFlow<Int?>
) {
    suspend fun getList(): List<CartAisleEntry> {
        val selectedStore = selectedStoreId.value
        if (selectedStore == null) {
            return emptyList()
        }

        val rawEntries = recipeDao.getRawShoppingList(selectedStore)
        return rawEntries.toHierarchy()
    }
}