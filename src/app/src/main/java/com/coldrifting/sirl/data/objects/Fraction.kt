package com.coldrifting.sirl.data.objects

import com.coldrifting.sirl.data.objects.Fraction.Companion.FractionAsStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.roundToInt

@Serializable(with = FractionAsStringSerializer::class)
data class Fraction(
    val numerator: Int,
    val denominator: Int = 1
) {
    fun toInt(): Int {
        return numerator / denominator
    }

    operator fun plus(other: Fraction): Fraction {
        if (this.denominator == other.denominator) {
            return Fraction(this.numerator + other.numerator, this.denominator)
        }

        val lcd = this.denominator * other.denominator
        val thisMultiplier = lcd / this.denominator
        val otherMultiplier = lcd / other.denominator

        return Fraction(
            (this.numerator * thisMultiplier) + (other.numerator * otherMultiplier),
            lcd
        ).simplify()
    }

    operator fun times(other: Fraction): Fraction {
        return Fraction(
            this.numerator * other.numerator,
            this.denominator * other.denominator
        ).simplify()
    }

    operator fun times(multiplier: Int): Fraction {
        return Fraction(numerator * multiplier, denominator).simplify()
    }

    operator fun div(divisor: Int): Fraction {
        return Fraction(numerator, denominator * divisor).simplify()
    }

    internal fun simplify(): Fraction {
        for (divisor in denominator downTo 2) {
            if (numerator % divisor == 0 && denominator % divisor == 0) {
                return Fraction(numerator / divisor, denominator / divisor)
            }
        }
        return this
    }

    override fun toString(): String {
        return if (denominator == 1) {
            "$numerator"
        } else if (numerator > denominator) {
            val fraction = if (numerator % denominator != 0) getFractionChar(
                Fraction(numerator % denominator, denominator).simplify()
            ) else ""

            "${numerator / denominator} $fraction".replace(" 0.", ".")
        } else {
            getFractionChar(this)
        }
    }

    fun toDecimalString(): String {
        return ((numerator * 1000 / denominator.toFloat()).roundToInt() / 1000.0f).toString()
    }

    fun isPlural(): Boolean {
        if ((numerator / denominator).toFloat() > 1.0f) {
            return true
        }
        return getFractionChar(this).contains(".")
    }

    companion object {
        fun fromInt(value: Int): Fraction {
            return Fraction(value, 1)
        }

        fun fromFloat(value: Float): Fraction {
            return fromFloatString(value.toString())
        }

        fun fromFloatString(valueAsString: String): Fraction {

            val value = valueAsString.toFloat()

            val whole = value.toInt()
            val partialFraction = value - whole

            when {
                valueAsString.endsWith(".16") ||
                        valueAsString.endsWith(".167") ||
                        valueAsString.endsWith(".166") -> return Fraction(whole * 6 + 1, 6)

                valueAsString.endsWith(".33") ||
                        valueAsString.endsWith(".334") ||
                        valueAsString.endsWith(".333") -> return Fraction(whole * 3 + 1, 3)

                valueAsString.endsWith(".66") ||
                        valueAsString.endsWith(".667") ||
                        valueAsString.endsWith(".666") -> return Fraction(whole * 3 + 2, 3)

                valueAsString.endsWith(".83") ||
                        valueAsString.endsWith(".834") ||
                        valueAsString.endsWith(".833") -> return Fraction(whole * 6 + 5, 6)
            }

            for (x in 2..16) {
                if ((partialFraction * x * 1000).toInt() % 1000 == 0) {
                    val num = ((whole + partialFraction) * x).toInt()
                    val dem = x
                    return Fraction(num, dem)
                }
            }

            // Fallback
            val num = (value * 1000).toInt()
            val dem = 1000
            return Fraction(num, dem)
        }

        private fun getFractionChar(fraction: Fraction): String {
            return when ("${fraction.numerator}/${fraction.denominator}") {
                "1/2" -> "½"

                "1/3",
                "33/100",
                "333/1000" -> "⅓"

                "2/3",
                "66/100",
                "666/1000" -> "⅔"

                "1/4" -> "¼"
                "3/4" -> "¾"

                "1/5" -> "⅕"
                "2/5" -> "⅖"
                "3/5" -> "⅗"
                "4/5" -> "⅘"

                "1/6" -> "⅙"
                "5/6" -> "⅚"

                "1/8" -> "⅛"
                "3/8" -> "⅜"
                "5/8" -> "⅝"
                "7/8" -> "⅞"

                else -> {
                    val test = (fraction.numerator / fraction.denominator.toFloat()).toString()
                        .trimEnd { i -> i == '0' }
                    test.substring(startIndex = 0, endIndex = 3.coerceIn(0, test.length))
                }
            }
        }

        object FractionAsStringSerializer : KSerializer<Fraction> {
            // Serial names of descriptors should be unique, this is why we advise including app package in the name.
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("com.coldrifting.sirl.Fraction", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: Fraction) {
                val num = value.numerator
                val dem = if (value.denominator != 1) "/${value.denominator}" else ""
                val string = "$num$dem"
                encoder.encodeString(string)
            }

            override fun deserialize(decoder: Decoder): Fraction {
                val parts = decoder.decodeString().split("/")
                if (parts.isEmpty() || parts.size > 2) {
                    throw SerializationException("Fraction in wrong format")
                }
                if (parts.size == 2) {
                    val (num: Int?, dem: Int?) = listOf(
                        parts[0].toIntOrNull(),
                        parts[1].toIntOrNull()
                    )
                    if (num == null || dem == null) {
                        throw SerializationException("Unable to decode fraction numerator or denominator")
                    }
                    return Fraction(num, dem)
                }
                val num: Int? = parts[0].toIntOrNull()
                if (num == null) {
                    throw SerializationException("Unable to decode fraction numerator")
                }
                return Fraction(num, 1)
            }
        }
    }
}