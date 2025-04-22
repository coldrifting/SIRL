package com.coldrifting.sirl.data.helper

data class ChecklistHeader(
    val id: Int,
    val name: String,
    val expanded: Boolean = true,
    val items: List<ChecklistItem>
) {
    companion object {
        fun List<ChecklistHeader>.toggleHeader(
            headerIndex: Int,
            state: Boolean? = null
        ): List<ChecklistHeader> {
            val state = state ?: !this[headerIndex].expanded

            val newList = this.toMutableList()
            newList[headerIndex] = this[headerIndex].copy(expanded = state)

            return newList
        }

        fun List<ChecklistHeader>.toggleItem(
            headerIndex: Int,
            itemIndex: Int
        ): List<ChecklistHeader> {
            val state = !this[headerIndex].items[itemIndex].checked

            val newItems = this[headerIndex].items.toMutableList()
            newItems[itemIndex] = this[headerIndex].items[itemIndex].copy(checked = state)

            val newList = this.toMutableList()
            newList[headerIndex] = this[headerIndex].copy(items = newItems)

            return newList
        }
    }
}