package com.coldrifting.sirl.data.objects

import app.cash.sqldelight.ColumnAdapter
import com.coldrifting.sirl.data.enums.UnitType
import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val fraction: Fraction,
    val type: UnitType = UnitType.Count
) {
    constructor(value: Int, type: UnitType = UnitType.Count) : this(Fraction.fromInt(value), type)
    constructor(value: Float, type: UnitType = UnitType.Count) : this(Fraction.fromFloat(value), type)

    override fun toString(): String {
        return "$fraction ${type.getAbbr(fraction.isPlural())}"
    }

    operator fun plus(other: Amount): Amount {
        if (this.type == other.type) {
            return Amount(this.fraction + other.fraction, this.type).simplify()
        }

        // Check for Mismatch
        if (this.type.toString().substring(0,3) != other.type.toString().substring(0,3)) {
            throw IllegalArgumentException("Invalid merging of incompatible unit types")
        }

        val divisor: Int
        val selectedType: UnitType
        if (this.type.getUnits() > other.type.getUnits()) {
            divisor = this.type.getUnits()
            selectedType = this.type
        }
        else {
            divisor = other.type.getUnits()
            selectedType = other.type
        }

        val newFraction = ((this.fraction * this.type.getUnits()) + (other.fraction * other.type.getUnits())) / divisor
        return Amount(newFraction, selectedType)
    }

    operator fun times(other: Amount): Amount {
        return Amount(this.fraction * other.fraction, this.type).simplify()
    }

    operator fun times(multiplier: Int): Amount {
        return Amount(this.fraction * multiplier, this.type).simplify()
    }

    private fun simplify(): Amount {
        if (type == UnitType.VolumeTeaspoons && fraction.toInt() >= 3) {
            return Amount(fraction / 3, UnitType.VolumeTablespoons)
        }

        if (type == UnitType.VolumeTablespoons && fraction.toInt() >= 16) {
            return Amount(fraction / 16, UnitType.VolumeCups)
        }

        return Amount(fraction, type)
    }

    fun encode(): String {
        return "${fraction.numerator}|${fraction.denominator}|$type"
    }

    companion object {
        object Adapter : ColumnAdapter<Amount, String> {
            override fun decode(databaseValue: String): Amount = Amount.decode(databaseValue)
            override fun encode(value: Amount): String = value.encode()
        }

        fun decode(value: String): Amount {
            val parts = value.split("|")
            if (parts.size != 3) {
                throw IllegalArgumentException()
            }

            val numerator = parts[0].toInt()
            val denominator = parts[1].toInt()
            val displayType = enumValueOf<UnitType>(parts[2])

            return Amount(Fraction(numerator, denominator), displayType)
        }
    }
}