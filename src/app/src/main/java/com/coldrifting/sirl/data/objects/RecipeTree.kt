package com.coldrifting.sirl.data.objects

data class RecipeTree(
    val recipeId: Int = 0,
    val recipeName: String = "",
    val recipeUrl: String? = null,
    val recipeSections: List<RecipeTreeSection> = listOf(),
    val recipeSteps: String? = null
)