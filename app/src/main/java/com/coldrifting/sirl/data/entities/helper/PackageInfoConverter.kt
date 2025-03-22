package com.coldrifting.sirl.data.entities.helper

import androidx.room.TypeConverter
import com.coldrifting.sirl.data.enums.ContainerType
import com.coldrifting.sirl.data.enums.UnitType

class PackageInfoConverter {
    @TypeConverter
    fun fromStringValue(stringValue: String?): PackageInfo? {
        if (stringValue == null) {
            return null
        }

        val arr = stringValue.split("|")
        if (arr.size != 3) {
            return null
        }

        return try {
            PackageInfo(
                enumValueOf<ContainerType>(arr[0]),
                enumValueOf<UnitType>(arr[1]),
                arr[2].toFloat()
            )
        } catch (e: Exception) {
            null
        }

    }

    @TypeConverter
    fun toStringValue(packageInfo: PackageInfo?): String? {
        packageInfo?.apply {
            return "$container|$units|$amount"
        }
        return null
    }
}