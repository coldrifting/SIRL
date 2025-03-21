package com.coldrifting.sirl.data.entities.joined

import androidx.room.Entity
import androidx.room.ForeignKey
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.entities.types.BayType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ItemAisles",
    primaryKeys = ["itemId", "aisleId"],
    foreignKeys = [
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Aisle::class,
            parentColumns = arrayOf("aisleId"),
            childColumns = arrayOf("aisleId"),
            onDelete = ForeignKey.CASCADE)
    ])
data class ItemAisle(
    val itemId: Int,
    val aisleId: Int,
    val bay: BayType = BayType.None
)