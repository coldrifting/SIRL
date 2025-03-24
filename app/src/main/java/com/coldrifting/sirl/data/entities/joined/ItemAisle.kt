package com.coldrifting.sirl.data.entities.joined

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.coldrifting.sirl.data.entities.Aisle
import com.coldrifting.sirl.data.entities.Item
import com.coldrifting.sirl.data.entities.Store
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
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Store::class,
            parentColumns = arrayOf("storeId"),
            childColumns = arrayOf("storeId"),
            onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = Aisle::class,
            parentColumns = arrayOf("aisleId"),
            childColumns = arrayOf("aisleId"),
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("itemId"),Index("storeId"),Index("aisleId")])
data class ItemAisle(
    val itemId: Int,
    val storeId: Int,
    val aisleId: Int,
    val bay: BayType = BayType.Middle
)