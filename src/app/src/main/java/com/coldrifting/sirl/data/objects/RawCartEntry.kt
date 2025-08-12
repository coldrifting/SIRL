package com.coldrifting.sirl.data.objects

data class RawCartEntry(
    val aisleId: Int,
    val aisleName: String,
    val itemId: Int,
    val itemName: String,
    val prepName: String?,
    val amount: Amount
)

