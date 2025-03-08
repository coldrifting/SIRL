package com.coldrifting.sirl.entities

import kotlinx.serialization.Serializable

@Serializable
data class Store(val storeId: Int, val name: String)