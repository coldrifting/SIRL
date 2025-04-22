package com.coldrifting.sirl.data.helper

data class RecipeTree(
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String = "",
    val recipeSections: List<RecipeTreeSection> = listOf(),
    val recipeSteps: String? = null
)