package com.coldrifting.sirl.entities

import kotlinx.serialization.Serializable

@Serializable
data class StoreLocation(val locationId: Int, val storeId: Int, val locationName: String)