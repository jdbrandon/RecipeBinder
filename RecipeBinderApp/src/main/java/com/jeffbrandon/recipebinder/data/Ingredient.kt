package com.jeffbrandon.recipebinder.data

import android.content.Context
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.enums.FractionalMeasurement
import com.jeffbrandon.recipebinder.enums.UnitType
import com.squareup.moshi.JsonClass

@SuppressWarnings("TooManyFunctions")
@JsonClass(generateAdapter = true)
data class Ingredient(val name: String, val amount: Float, val unit: UnitType) {

    fun amountString(context: Context, useLongUnits: Boolean = false): CharSequence {
        val num = StringBuilder()
        if (amountWhole() > 0) num.append(amountWhole())

        num.appendFractionalComponent(context)
        num.appendUnit(context, useLongUnits)

        return num.toString()
    }

    fun amountWhole() = amount.toInt()

    /**
     * Ranges used to capture floating point and conversion rounding
     */
    @SuppressWarnings("MagicNumber")
    fun amountFraction(): FractionalMeasurement = when (((amount - amountWhole()) * 100).toInt()) {
        in 0..12 -> FractionalMeasurement.ZERO
        in 13..29 -> FractionalMeasurement.QUARTER
        in 30..41 -> FractionalMeasurement.THIRD
        in 42..58 -> FractionalMeasurement.HALF
        in 59..70 -> FractionalMeasurement.THIRD_TWO
        in 71..82 -> FractionalMeasurement.QUARTER_THREE
        in 82..100 -> FractionalMeasurement.ROUND_UP
        else -> error("I don't know how math works for ingredient conversions")
    }

    fun convertTo(type: UnitType): Ingredient = when (unit) {
        UnitType.GALLON -> gallonTo(type)
        UnitType.QUART -> quartTo(type)
        UnitType.PINT -> pintTo(type)
        UnitType.CUP -> cupTo(type)
        UnitType.OUNCE -> ounceTo(type)
        UnitType.TABLE_SPOON -> tableSpoonTo(type)
        UnitType.TEA_SPOON -> teaSpoonTo(type)
        UnitType.POUND -> poundTo(type)
        UnitType.LITER -> literTo(type)
        UnitType.MILLILITER -> milliLiterTo(type)
        UnitType.GRAM -> gramTo(type)
        UnitType.NONE -> amount
    }.let {
        this.copy(amount = it, unit = type)
    }

    private fun StringBuilder.appendFractionalComponent(context: Context) {
        when (amountFraction()) {
            FractionalMeasurement.ZERO -> Unit
            FractionalMeasurement.QUARTER -> {
                if (isNotEmpty()) append(" ")
                append(context.getString(R.string._1_quarter))
            }
            FractionalMeasurement.THIRD -> {
                if (isNotEmpty()) append(" ")
                append(context.getString(R.string._1_third))
            }
            FractionalMeasurement.HALF -> {
                if (isNotEmpty()) append(" ")
                append(context.getString(R.string._1_half))
            }
            FractionalMeasurement.THIRD_TWO -> {
                if (isNotEmpty()) append(" ")
                append(context.getString(R.string._2_thirds))
            }
            FractionalMeasurement.QUARTER_THREE -> {
                if (isNotEmpty()) append(" ")
                append(context.getString(R.string._3_quarters))
            }
            FractionalMeasurement.ROUND_UP -> {
                clear()
                append(amountWhole() + 1)
            }
        }
    }

    private fun StringBuilder.appendUnit(context: Context, useLongUnits: Boolean) {
        when (unit) {
            UnitType.NONE -> Unit
            else -> {
                if (isNotEmpty()) append(" ")
                append(unit.getString(context, useLongUnits))
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun gallonTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON, UnitType.NONE -> amount
            UnitType.QUART -> amount * 4
            UnitType.PINT -> amount * 8
            UnitType.CUP -> amount * 16
            UnitType.OUNCE -> amount * 128
            UnitType.TABLE_SPOON -> amount * 256
            UnitType.TEA_SPOON -> amount * 768
            UnitType.LITER -> amount * 3.78541f
            UnitType.MILLILITER -> amount * 3785.41f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert gallon to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun quartTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 4
            UnitType.QUART, UnitType.NONE -> amount
            UnitType.PINT -> amount * 2
            UnitType.CUP -> amount * 4
            UnitType.OUNCE -> amount * 32
            UnitType.TABLE_SPOON -> amount * 64
            UnitType.TEA_SPOON -> amount * 192
            UnitType.LITER -> amount * .946353f
            UnitType.MILLILITER -> amount * 946.353f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert quart to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun pintTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 8
            UnitType.QUART -> amount / 2
            UnitType.PINT, UnitType.NONE -> amount
            UnitType.CUP -> amount * 2
            UnitType.OUNCE -> amount * 16
            UnitType.TABLE_SPOON -> amount * 32
            UnitType.TEA_SPOON -> amount * 96
            UnitType.LITER -> amount * 0.473176f
            UnitType.MILLILITER -> amount * 473.176f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert pint to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun cupTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 16
            UnitType.QUART -> amount / 4
            UnitType.PINT -> amount / 2
            UnitType.CUP, UnitType.NONE -> amount
            UnitType.OUNCE -> amount * 8
            UnitType.TABLE_SPOON -> amount * 16
            UnitType.TEA_SPOON -> amount * 48
            UnitType.LITER -> amount * 0.236588f
            UnitType.MILLILITER -> amount * 236.588f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert cup to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun ounceTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 128
            UnitType.QUART -> amount / 32
            UnitType.PINT, UnitType.POUND -> amount / 16
            UnitType.CUP -> amount / 8
            UnitType.TABLE_SPOON -> amount * 2
            UnitType.TEA_SPOON -> amount * 6
            UnitType.LITER -> amount * 0.0295735f
            UnitType.MILLILITER -> amount * 29.5735f
            UnitType.GRAM -> amount * 28.3495f
            UnitType.OUNCE, UnitType.NONE -> amount
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun tableSpoonTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 256
            UnitType.QUART -> amount / 64
            UnitType.PINT -> amount / 32
            UnitType.CUP -> amount / 16
            UnitType.OUNCE -> amount / 2
            UnitType.TABLE_SPOON, UnitType.NONE -> amount
            UnitType.TEA_SPOON -> amount * 3
            UnitType.LITER -> amount * 0.0147868f
            UnitType.MILLILITER -> amount * 14.7868f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert table spoon to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun teaSpoonTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount / 768
            UnitType.QUART -> amount / 192
            UnitType.PINT -> amount / 96
            UnitType.CUP -> amount / 48
            UnitType.OUNCE -> amount / 6
            UnitType.TABLE_SPOON -> amount / 3
            UnitType.TEA_SPOON, UnitType.NONE -> amount
            UnitType.LITER -> amount * 0.00492892f
            UnitType.MILLILITER -> amount * 4.92892f
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert tea spoon to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun poundTo(type: UnitType): Float {
        return when (type) {
            UnitType.GRAM -> amount * 453.592f
            UnitType.POUND, UnitType.NONE -> amount
            UnitType.OUNCE -> amount * 16
            else -> throw IllegalArgumentException("Cannot convert pound to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun literTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON -> amount * 0.264172f
            UnitType.QUART -> amount * 1.05669f
            UnitType.PINT -> amount * 2.11338f
            UnitType.CUP -> amount * 4.22675f
            UnitType.OUNCE -> amount * 33.814f
            UnitType.TABLE_SPOON -> amount * 67.628f
            UnitType.TEA_SPOON -> amount * 202.884f
            UnitType.LITER, UnitType.NONE -> amount
            UnitType.MILLILITER -> amount * 1000
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert liter to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun milliLiterTo(type: UnitType): Float {
        return when (type) {
            UnitType.GALLON,
            UnitType.QUART,
            UnitType.PINT,
            UnitType.CUP,
            UnitType.OUNCE,
            UnitType.TABLE_SPOON,
            UnitType.TEA_SPOON,
            -> literTo(type) / 1000.0f
            UnitType.LITER -> amount / 1000.0f
            UnitType.MILLILITER, UnitType.NONE -> amount
            UnitType.POUND, UnitType.GRAM -> throw IllegalArgumentException("Cannot convert milliliter to $type")
        }
    }

    @SuppressWarnings("MagicNumber")
    private fun gramTo(type: UnitType): Float {
        return when (type) {
            UnitType.GRAM, UnitType.NONE -> amount
            UnitType.POUND -> amount * 0.00220462f
            UnitType.OUNCE -> amount * 0.035274f
            else -> throw IllegalArgumentException("Cannot convert gram to $type")
        }
    }
}
