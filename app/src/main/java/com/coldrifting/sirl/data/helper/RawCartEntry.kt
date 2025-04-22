package com.coldrifting.sirl.data.helper

import com.coldrifting.sirl.data.enums.UnitType

data class RawCartEntry(
    val aisleId: Int,
    val aisleName: String,
    val itemId: Int,
    val itemName: String,
    val prepName: String?,
    val unitType: UnitType,
    val totalAmount: Float
)

