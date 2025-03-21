package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coldrifting.sirl.entities.types.ItemCategory
import com.coldrifting.sirl.entities.types.PackageType
import com.coldrifting.sirl.entities.types.UnitType
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    val itemName: String,
    val itemCategory: ItemCategory,
    val packageType: PackageType? = null,
    val packageUnits: UnitType? = null,
    val packageAmount: Float? = null
)