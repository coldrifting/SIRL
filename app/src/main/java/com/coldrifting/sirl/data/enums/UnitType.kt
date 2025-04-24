package com.coldrifting.sirl.data.enums

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

fun UnitType.getPrepAbbreviation(amount: Int): String {
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
        else -> "$leading.$amountFraction".trimEnd { it == '0' }.trimEnd { it == '.' }.replace(Regex("^\\."), "0.")
    }

    val plural = amount > 1000

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