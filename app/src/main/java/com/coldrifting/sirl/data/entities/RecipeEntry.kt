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
    @PrimaryKey(autoGenerate = true)
    val recipeEntryId: Int = 0,

    val recipeId: Int = -1,
    val recipeSectionId: Int = -1,
    val itemId: Int = -1,
    val itemPrepId: Int? = null,

    val unitType: UnitType = UnitType.EACHES,
    val amount: Float = 0.0f
)