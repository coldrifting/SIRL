package com.coldrifting.sirl.data.objects

data class RecipeTreeSection(
    val sectionId: Int,
    val sectionName: String,
    val sortOrder: Int,
    val items: List<RecipeTreeItemEntry>
)