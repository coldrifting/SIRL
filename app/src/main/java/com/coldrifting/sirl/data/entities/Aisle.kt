package com.coldrifting.sirl.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Aisle(
    val aisleId: Int,
    val storeId: Int,
    val aisleName: String,
    val sortingPrefix: Int = 0
)
