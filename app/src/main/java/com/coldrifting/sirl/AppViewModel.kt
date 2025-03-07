package com.coldrifting.sirl

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel: ViewModel() {
    private val _selectedStore = MutableStateFlow(-1)
    val selectedStore = _selectedStore.asStateFlow()

    private var idIndex = 2
    private val _stores = MutableStateFlow(mapOf(Pair(0, "Macey's"), Pair(1,"Harmon's")))
    val stores = _stores.asStateFlow()

    private val _aisles = MutableStateFlow(mapOf(Pair(0, listOf("Aisle 1", "Bakery", "AAA", "Testing"))))
    val aisles = _aisles.asStateFlow()

    fun getStore(id: Int): String? {
        return _stores.value[id]
    }

    fun selectStore(id: Int) {
        _selectedStore.value = id
    }

    fun addStore(name: String) {
        _stores.update { oldVal ->
            (oldVal + (idIndex++ to name))
        }
    }

    fun deleteStore(id: Int) {
        _stores.update { oldVal ->
            (oldVal - id)
        }
    }

    fun renameStore(id: Int, newName: String) {
        _stores.update { oldVal ->
            (oldVal + (id to newName))
        }
    }

    fun swapAisles(id: Int, from: Int, to: Int) {
        val aisles = _aisles.value[id] ?: return
        _aisles.update {
            _aisles.value + (id to aisles.toMutableList().apply{add(to, removeAt(from))})
        }
    }

    fun addAisle(id: Int, aisleName: String) {
        val aisles = _aisles.value[id] ?: listOf()
        if (aisles.contains(aisleName)) return
        _aisles.update {
            _aisles.value + (id to aisles.toMutableList().apply{add(aisleName)})
        }
    }

    fun renameAisle(id: Int, index: Int, newAisleName: String) {
        val aisles = _aisles.value[id] ?: return
        if (aisles.contains(newAisleName)) return
        for(i in aisles.indices) {
            if (aisles[i].hashCode() == index) {
                _aisles.update {
                    _aisles.value + (id to aisles.toMutableList().apply { set(i, newAisleName) })
                }
                break
            }
        }
    }

    fun deleteAisle(id: Int, index: Int) {
        val aisles = _aisles.value[id] ?: return
        for(i in aisles.indices) {
            if (aisles[i].hashCode() == index) {
                _aisles.update {
                    _aisles.value + (id to aisles.toMutableList().apply { removeAt(i) })
                }
                break
            }
        }
    }
}