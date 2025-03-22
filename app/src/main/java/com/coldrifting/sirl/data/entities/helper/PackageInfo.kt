package com.coldrifting.sirl.data.entities.helper

import com.coldrifting.sirl.data.enums.ContainerType
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.serialization.Serializable

@Serializable
data class PackageInfo(val container: ContainerType, val units: UnitType, val amount: Float)
