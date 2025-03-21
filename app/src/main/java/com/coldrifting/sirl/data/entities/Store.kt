package com.coldrifting.sirl.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Stores")
data class Store(
    @PrimaryKey(autoGenerate = true)
    var storeId: Int = 0,
    var storeName: String)