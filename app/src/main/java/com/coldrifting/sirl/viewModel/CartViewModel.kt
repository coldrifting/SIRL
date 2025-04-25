package com.coldrifting.sirl.viewModel

import com.coldrifting.sirl.repo.AppRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(private val repository: AppRepo) {
    val list = repository.cart.list

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