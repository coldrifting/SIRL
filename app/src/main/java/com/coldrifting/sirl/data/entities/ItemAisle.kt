package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.coldrifting.sirl.data.enums.BayType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ItemAisles",
    primaryKeys = ["itemId", "storeId"],
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = Store::class,
            parentColumns = arrayOf("storeId"),
            childColumns = arrayOf("storeId"),
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = Aisle::class,
            parentColumns = arrayOf("aisleId"),
            childColumns = arrayOf("aisleId"),
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("itemId"), Index("storeId"), Index("aisleId")])
data class ItemAisle(
    val itemId: Int,
    val storeId: Int,
    val aisleId: Int,
    val bay: BayType = BayType.Middle
)