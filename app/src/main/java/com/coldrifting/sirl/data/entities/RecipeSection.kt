package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "RecipeSections",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("recipeId"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("recipeId")]
)
data class RecipeSection(
    @PrimaryKey(autoGenerate = true)
    val recipeSectionId: Int = 0,
    val recipeId: Int,
    val sectionName: String,
    val sortIndex: Int = 0
)
