package com.coldrifting.sirl.view

import com.coldrifting.sirl.repo.AppRepository
import com.coldrifting.sirl.data.helper.ChecklistHeader
import com.coldrifting.sirl.data.helper.ChecklistItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(private val repository: AppRepository) {
    // TODO - Move to DB for persistence
    private val _list = MutableStateFlow<List<ChecklistHeader>?>(null)
    val list = _list.asStateFlow()

    fun getList() {
        repository.scope.launch {
            val rawList = repository.cart.getList()
            val updatedList = rawList.map { entry ->
                ChecklistHeader(
                    id = entry.aisleId,
                    name = entry.aisleName,
                    expanded = true,
                    items = entry.entries.map { items ->
                        ChecklistItem(
                            id = items.itemId,
                            name = items.itemName,
                            details = items.amount
                        )
                    })
            }

            _list.update {
                return@update updatedList
            }
        }
    }

    fun cartHeaderClicked(headerIndex: Int) {
        _list.update { list ->
            if (list == null) {
                return@update null
            }

            val newList = list.toMutableList()
            newList[headerIndex] = list[headerIndex].copy(expanded = !list[headerIndex].expanded)

            return@update newList
        }
    }

    fun cartItemClicked(headerIndex: Int, itemIndex: Int) {
        _list.update { list ->
            if (list == null) {
                return@update null
            }

            var items = list[headerIndex].items.toMutableList()
            items[itemIndex] = items[itemIndex].copy(checked = !items[itemIndex].checked)

            var newList = list.toMutableList()
            newList[headerIndex] = newList[headerIndex].copy(items = items)
            if (newList[headerIndex].items.all { i -> i.checked }) {
                // Collapse header after delay
                repository.scope.launch {
                    delay(200)
                    _list.update { list ->
                        if (list == null) {
                            return@update null
                        }

                        var newNewList = list.toMutableList()
                        newNewList[headerIndex] = list[headerIndex].copy(expanded = false)
                        newNewList
                    }
                }
            }

            return@update newList
        }
    }
}