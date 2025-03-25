package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var recipeId: Int = 0,
    var recipeName: String,
    var recipeUrl: String = "",
    var pinned: Boolean = false)