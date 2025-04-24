package com.coldrifting.sirl.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val storeId: Int,
    val storeName: String,
    val selected: Boolean = false,
)
