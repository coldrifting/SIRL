package com.coldrifting.sirl.data.helper

data class RecipeTreeSection(
    val sectionId: Int,
    val sectionName: String,
    val sortOrder: Int,
    val items: List<RecipeTreeItemEntry>
)