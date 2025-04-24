package com.coldrifting.sirl.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class ItemPrep(
    val itemPrepId: Int,
    val itemId: Int,
    val prepName: String
)
