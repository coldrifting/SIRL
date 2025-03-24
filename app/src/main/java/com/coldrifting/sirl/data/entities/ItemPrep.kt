package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ItemPreps",
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("itemId")]
)
data class ItemPrep(
    @PrimaryKey(autoGenerate = true)
    val itemPrepId: Int = 0,
    val itemId: Int,
    val prepName: String)