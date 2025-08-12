package com.coldrifting.sirl.data.enums

import app.cash.sqldelight.ColumnAdapter

enum class UnitType {
    Count,
    VolumeTeaspoons,
    VolumeTablespoons,
    VolumeOunces,
    VolumeCups,
    VolumeQuarts,
    VolumePints,
    VolumeGallons,
    WeightOunces,
    WeightPounds;

    object Adapter : ColumnAdapter<UnitType, String> {
        override fun decode(databaseValue: String): UnitType = enumValueOf<UnitType>(databaseValue)
        override fun encode(value: UnitType): String = value.toString()
    }

    fun getFriendlyName(): String {
        return when(this) {
            Count -> "Count"
            VolumeTeaspoons -> "Teaspoons"
            VolumeTablespoons -> "Tablespoons"
            VolumeOunces -> "Ounces"
            VolumeCups -> "Cups"
            VolumePints -> "Pints"
            VolumeQuarts -> "Quarts"
            VolumeGallons -> "Gallons"
            WeightOunces -> "Ounces (#)"
            WeightPounds -> "Pounds"
        }
    }

    fun getAbbr(plural: Boolean = false): String {
        return when(this) {
            Count -> "ea."
            VolumeTeaspoons -> "tsp"
            VolumeTablespoons -> "Tbsp"
            VolumeOunces -> "oz."
            VolumeCups -> if (plural) "cups" else "cup"
            VolumePints -> "pt."
            VolumeQuarts -> "qt."
            VolumeGallons -> "gal."
            WeightOunces -> "oz."
            WeightPounds -> if (plural) "lbs" else "lb"
        }
    }

    // Number of teaspoons or ounces per unit for volume or weight
    fun getUnits(): Int {
        return when (this) {
            Count -> 1

            VolumeTeaspoons -> 1
            VolumeTablespoons -> 3
            VolumeOunces -> 6
            VolumeCups -> 48
            VolumeQuarts -> 96
            VolumePints -> 192
            VolumeGallons -> 768

            WeightOunces -> 1
            WeightPounds -> 16
        }
    }
}