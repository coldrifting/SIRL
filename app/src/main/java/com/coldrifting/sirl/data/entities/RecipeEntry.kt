package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "RecipeEntries",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("recipeId"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = RecipeSection::class,
            parentColumns = arrayOf("recipeSectionId"),
            childColumns = arrayOf("recipeSectionId"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = ItemPrep::class,
            parentColumns = arrayOf("itemPrepId"),
            childColumns = arrayOf("itemPrepId"),
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index("recipeId"),
        Index("recipeSectionId"),
        Index("itemId"),
        Index("itemPrepId"),
        Index("recipeId", "recipeSectionId", "itemId", "itemPrepId", unique = true)
    ]
)
data class RecipeEntry(
    @PrimaryKey
    val recipeEntryId: Int = 0,

    val recipeId: Int,
    val recipeSectionId: Int?,
    val itemId: Int,
    val itemPrepId: Int?,

    val unitType: UnitType,
    val amount: Float
)