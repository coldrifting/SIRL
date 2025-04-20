package com.coldrifting.sirl.data.enums

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

enum class UnitType {
    EACHES,
    Teaspoons,
    Tablespoons,
    Cups,
    Quarts,
    Pints,
    Gallons,
    Ounces,
    Pounds
}

fun UnitType.getPrepAbbreviation(amount: Float): String {
    // Halves -> 0.5
    // Thirds -> 0.33, 0.66
    // Fourths -> 0.25, 0.75,
    // Fifths -> 0.2, 0.4, 0.6, 0.8
    // Sixths -> 0.166, 0.833
    // Eights -> 0.125, 0.325, 0.625, 0.875
    val amountInt: String = if (amount.toInt() >= 1) amount.toInt().toString() + " " else ""
    val amountFraction: Float = amount - amount.toInt()
    val amountAbbrev = when (round(amountFraction * 1000).toInt()) {
        500 -> "$amountInt½"

        330 -> "$amountInt⅓"
        660 -> "$amountInt⅔"

        250 -> "$amountInt¼"
        750 -> "$amountInt¾"

        200 -> "$amountInt⅕"
        400 -> "$amountInt⅖"
        600 -> "$amountInt⅗"
        800 -> "$amountInt⅘"

        166 -> "$amountInt⅙"
        833 -> "$amountInt⅚"

        125 -> "$amountInt⅛"
        325 -> "$amountInt⅜"
        625 -> "$amountInt⅝"
        875 -> "$amountInt⅞"

        // Trim leading 0
        else -> BigDecimal(amount.toString()).setScale(3, RoundingMode.HALF_UP).toString()
            .trimEnd { it == '0' }.trimEnd { it == '.' }
    }

    val plural = amount > 1

    val unitAbbrev: String = when (this) {
        UnitType.EACHES -> "ea."
        UnitType.Teaspoons -> "tsp"
        UnitType.Tablespoons -> "Tbsp"
        UnitType.Cups -> if (plural) "cups" else "cup"
        UnitType.Quarts -> "qt."
        UnitType.Pints -> "pt."
        UnitType.Gallons -> "gal."
        UnitType.Ounces -> "oz."
        UnitType.Pounds -> if (plural) "lbs" else "lb"
    }


    return "$amountAbbrev $unitAbbrev"
}