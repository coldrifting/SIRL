package com.coldrifting.sirl.viewModel

import com.coldrifting.sirl.data.objects.Amount
import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(private val repository: AppRepo) {
    val list = repository.cart.list
    val selectedRecipes = repository.cart.selectedRecipes
    val availableRecipes = repository.cart.availableRecipes

    val selectedItems = repository.cart.selectedItems
    val availableItems = repository.cart.availableItems

    private val _locationWarning = MutableStateFlow(listOf<String>())
    val locationWarning = _locationWarning.asStateFlow()

    fun generateList() {
        repository.cart.generateList()
        _locationWarning.update {
            repository.cart.getItemsWithUnknownLocation()
        }
    }

    fun clearUnknownLocationWarning() {
        _locationWarning.update {
            listOf()
        }
    }

    fun clearList() {
        repository.cart.clearList()
    }

    fun clearCartSelection() = repository.cart.clearCartSelection()

    fun addRecipeToCart(recipeId: Int) =
        repository.cart.addRecipeToCart(recipeId)

    fun removeRecipeFromCart(recipeId: Int) =
        repository.cart.removeRecipeFromCart(recipeId)

    fun updateRecipeInCart(recipeId: Int, newAmount: Int) =
        repository.cart.updateRecipeInCart(recipeId, newAmount)

    fun addItemToCart(itemId: Int, amount: Amount) =
        repository.cart.addItemToCart(itemId, amount)

    fun updateItemInCart(itemId: Int, amount: Amount) =
        repository.cart.updateItemInCart(itemId, amount)

    fun removeItemFromCart(itemId: Int) =
        repository.cart.removeItemFromCart(itemId)

    fun toggleCartHeaderExpanded(cartHeaderId: Int) =
        repository.cart.toggleCartHeaderExpanded(cartHeaderId)

    fun toggleCartItemChecked(cartHeaderId: Int, cartItemId: Int) {
        repository.cart.toggleCartItemChecked(cartItemId)

        repository.scope.launch {
            delay(200)
            if (list.value.first{i -> i.id == cartHeaderId}.items.all{i -> i.checked}) {
                toggleCartHeaderExpanded(cartHeaderId)
            }
        }
    }
}