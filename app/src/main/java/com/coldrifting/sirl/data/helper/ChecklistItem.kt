package com.coldrifting.sirl.data.helper

data class ChecklistItem(
    val id: Int,
    val name: String,
    val info: String? = null,
    val details: String? = null,
    val checked: Boolean = false
)