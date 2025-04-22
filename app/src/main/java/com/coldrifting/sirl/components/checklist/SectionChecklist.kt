package com.coldrifting.sirl.components.checklist

data class CheckHeader(
    val id: Int,
    val name: String,
    val expanded: Boolean = true,
    val items: List<CheckItem>
)

data class CheckItem(
    val id: Int,
    val name: String,
    val info: String? = null,
    val details: String? = null,
    val checked: Boolean = false
)

fun List<CheckHeader>.toggleHeader(headerIndex: Int, state: Boolean? = null): List<CheckHeader> {
    val state = state ?: !this[headerIndex].expanded

    val newList = this.toMutableList()
    newList[headerIndex] = this[headerIndex].copy(expanded = state)

    return newList
}

fun List<CheckHeader>.toggleItem(headerIndex: Int, itemIndex: Int): List<CheckHeader> {
    val state = !this[headerIndex].items[itemIndex].checked

    val newItems = this[headerIndex].items.toMutableList()
    newItems[itemIndex] = this[headerIndex].items[itemIndex].copy(checked = state)

    val newList = this.toMutableList()
    newList[headerIndex] = this[headerIndex].copy(items = newItems)

    return newList
}