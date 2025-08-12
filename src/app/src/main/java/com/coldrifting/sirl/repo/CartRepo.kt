package com.coldrifting.sirl.repo

import com.coldrifting.sirl.Database
import com.coldrifting.sirl.data.objects.Amount
import com.coldrifting.sirl.data.objects.CartAisleEntry.Companion.toHierarchy
import com.coldrifting.sirl.data.objects.CartItemSelect
import com.coldrifting.sirl.data.objects.CartRecipeSelect
import com.coldrifting.sirl.data.objects.ChecklistHeader
import com.coldrifting.sirl.data.objects.ChecklistItem
import com.coldrifting.sirl.data.objects.RawCartEntry
import com.coldrifting.sirl.repo.utils.toListStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class CartRepo(
    private val db: Database,
    scope: CoroutineScope,
    private val selectedStoreId: StateFlow<Int?>
) {
    val list = db.cartQueries.getCartList().toListStateFlow(scope) { list ->

        val map = mutableMapOf<Int, ChecklistHeader>()

        list.forEach { entry ->
            val header = map[entry.headerId]

            val newHeader = ChecklistHeader(
                id = entry.headerId,
                name = entry.headerName,
                expanded = entry.headerExpanded,
                items = (header?.items ?: listOf()) + ChecklistItem(
                    id = entry.cartItemId,
                    name = entry.cartItemName,
                    details = entry.cartItemDetails,
                    checked = entry.cartItemChecked))

            map[entry.headerId] = newHeader
        }

        map.values.toList()
    }

    val selectedRecipes = db.cartQueries.getSelectedShoppingRecipes().toListStateFlow(scope) { list ->
        list.map{ CartRecipeSelect(it.recipeId, it.recipeName, it.recipeQuantity.toInt()) }
    }

    val selectedItems = db.cartQueries.getSelectedShoppingItems().toListStateFlow(scope) { list ->
        list.map{ CartItemSelect(it.itemId, it.itemName, it.amount) }
    }

    val availableRecipes = db.cartQueries.getAvailableRecipesForCart().toListStateFlow(scope)
    val availableItems = db.cartQueries.getAvailableItemsForCart().toListStateFlow(scope)

    fun toggleCartHeaderExpanded(cartHeaderId: Int) {
        val expanded = db.cartQueries.getCartHeaderExpanded(cartHeaderId).executeAsOne()
        db.cartQueries.toggleCartHeaderExpanded(!expanded, cartHeaderId)
    }

    fun toggleCartItemChecked(cartItemId: Int) {
        val checked = db.cartQueries.getCartItemChecked(cartItemId).executeAsOne()
        db.cartQueries.toggleCartItemChecked(!checked, cartItemId)
    }

    fun getItemsWithUnknownLocation(): List<String> {
        return db.cartQueries.getItemsWithUnknownLocation().executeAsList()
    }

    fun clearList() {
        db.cartQueries.clearCartHeaders()
        db.cartQueries.clearCartItems()
    }

    fun clearCartSelection() {
        db.cartQueries.clearSelectedCartRecipes()
        db.cartQueries.clearSelectedCartItems()
    }

    fun addRecipeToCart(recipeId: Int) {
        db.cartQueries.addRecipeToCart(recipeId)
    }

    fun updateRecipeInCart(recipeId: Int, newAmount: Int) {
        db.cartQueries.updateRecipeInCart(newAmount, recipeId)
    }

    fun removeRecipeFromCart(recipeId: Int) {
        db.cartQueries.removeRecipeFromCart(recipeId)
    }

    fun addItemToCart(itemId: Int, amount: Amount) {
        db.cartQueries.addItemToCart(itemId, amount)
    }

    fun updateItemInCart(itemId: Int, amount: Amount) {
        db.cartQueries.updateItemInCart(amount, itemId)
    }

    fun removeItemFromCart(itemId: Int) {
        db.cartQueries.removeItemFromCart(itemId)
    }

    fun generateList() {
        val selectedStore = selectedStoreId.value
        if (selectedStore == null) {
            return
        }

        val rawRecipeEntries = db.cartQueries.getShoppingList(selectedStore).executeAsList().map {
            RawCartEntry(
                it.aisleId ?: -1,
                it.aisleName ?: "Unknown Location",
                it.itemId,
                it.itemName,
                it.prepName,
                it.amount * it.cartAmount
            )
        }
        val rawItemEntries = db.cartQueries.getSelectedShoppingItemsWithAisle(selectedStore).executeAsList().map {
            RawCartEntry(
                it.aisleId ?: -1,
                it.aisleName ?: "Unknown Location",
                it.itemId,
                it.itemName,
                null,
                it.amount
            )
        }

        // TODO - Don't attempt to merge dissimilar unit types
        val entries = (rawRecipeEntries + rawItemEntries)
            .groupingBy { it.itemId }
            .reduce{i, a, r -> RawCartEntry(a.aisleId, a.aisleName, a.itemId, a.itemName, a.prepName, a.amount + r.amount ) }.values.toList()
            .toHierarchy()

        db.cartQueries.transaction {
            db.cartQueries.clearCartHeaders()
            db.cartQueries.clearCartItems()
            entries.forEach { entry ->
                db.cartQueries.insertCartHeader(entry.aisleId, entry.aisleName)
                entry.entries.forEach { subEntry ->
                    db.cartQueries.insertCartItem(
                        subEntry.itemId,
                        entry.aisleId,
                        subEntry.itemName,
                        subEntry.amount
                    )
                }
            }
        }


    }
}