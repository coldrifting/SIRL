package com.coldrifting.sirl.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val recipeId: Int,
    val recipeName: String,
    val url: String? = null,
    val pinned: Boolean = false,
    val steps: String? = null
)
