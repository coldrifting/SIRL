package com.coldrifting.sirl.data.entities

import com.coldrifting.sirl.data.enums.BayType
import kotlinx.serialization.Serializable

@Serializable
data class ItemAisle(
    val itemId: Int,
    val storeId: Int,
    val aisleId: Int,
    val bay: BayType = BayType.Middle
)
