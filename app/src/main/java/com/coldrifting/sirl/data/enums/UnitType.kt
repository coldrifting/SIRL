package com.coldrifting.sirl.data.enums

import app.cash.sqldelight.ColumnAdapter

enum class UnitType {
    Count,
    Teaspoons,
    Tablespoons,
    Cups,
    Quarts,
    Pints,
    Gallons,
    Ounces,
    Pounds;

    object Adapter : ColumnAdapter<UnitType, String> {
        override fun decode(databaseValue: String): UnitType = enumValueOf<UnitType>(databaseValue)
        override fun encode(value: UnitType): String = value.toString()
    }

    fun getPrepAbbreviation(amount: Int): String {
        var nonFraction = amount / 1000
        val leading = if (nonFraction >= 1) "$nonFraction " else ""

        val amountFraction = amount % 1000
        val amountAbbrev = when (amountFraction) {
            500 -> "$leading½"

            330 -> "$leading⅓"
            333 -> "$leading⅓"
            660 -> "$leading⅔"
            666 -> "$leading⅔"

            250 -> "$leading¼"
            750 -> "$leading¾"

            200 -> "$leading⅕"
            400 -> "$leading⅖"
            600 -> "$leading⅗"
            800 -> "$leading⅘"

            166 -> "$leading⅙"
            833 -> "$leading⅚"

            125 -> "$leading⅛"
            325 -> "$leading⅜"
            625 -> "$leading⅝"
            875 -> "$leading⅞"

            // Trim leading and trailing 0 and dot
            else -> "$leading.$amountFraction".trimEnd { it == '0' }.trimEnd { it == '.' }
                .replace(Regex("^\\."), "0.")
        }

        val plural = amount > 1000

        val unitAbbrev: String = when (this) {
            Count -> "ea."
            Teaspoons -> "tsp"
            Tablespoons -> "Tbsp"
            Cups -> if (plural) "cups" else "cup"
            Quarts -> "qt."
            Pints -> "pt."
            Gallons -> "gal."
            Ounces -> "oz."
            Pounds -> if (plural) "lbs" else "lb"
        }

        return "$amountAbbrev $unitAbbrev".replace("  ", " ")
    }
}