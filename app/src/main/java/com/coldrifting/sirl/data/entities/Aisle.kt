package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Aisles",
    foreignKeys = [
        ForeignKey(
            entity = Store::class,
            parentColumns = arrayOf("storeId"),
            childColumns = arrayOf("storeId"),
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("storeId")]
)
data class Aisle(
    @PrimaryKey(autoGenerate = true)
    val aisleId: Int = 0,
    val storeId: Int,
    val aisleName: String,
    val sortingPrefix: Int = 0)