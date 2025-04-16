package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val recipeId: Int = 0,
    val recipeName: String,
    val recipeUrl: String = "",
    val pinned: Boolean = false,
    val recipeSteps: String? = null
)