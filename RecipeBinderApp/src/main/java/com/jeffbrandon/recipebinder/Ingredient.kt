package com.jeffbrandon.recipebinder

data class Ingredient(val name: String, val amount: Float, val unit: UnitType) {

    fun convertTo(type: UnitType): Float {
        return when(unit) {
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
        }
    }

    private fun gallonTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount
            UnitType.QUART -> amount * 4
            UnitType.PINT -> amount * 8
            UnitType.CUP -> amount * 16
            UnitType.OUNCE -> amount * 128
            UnitType.TABLE_SPOON -> amount * 256
            UnitType.TEA_SPOON -> amount * 768
            UnitType.LITER -> amount * 3.78541f
            UnitType.MILLILITER -> amount * 3785.41f
            else -> throw IllegalArgumentException("Cannot convert gallon to $type")
        }
    }

    private fun quartTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 4
            UnitType.QUART -> amount
            UnitType.PINT -> amount * 2
            UnitType.CUP -> amount * 4
            UnitType.OUNCE -> amount * 32
            UnitType.TABLE_SPOON -> amount * 64
            UnitType.TEA_SPOON -> amount * 192
            UnitType.LITER -> amount * .946353f
            UnitType.MILLILITER -> amount * 946.353f
            else -> throw IllegalArgumentException("Cannot convert quart to $type")
        }
    }

    private fun pintTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 8
            UnitType.QUART -> amount / 2
            UnitType.PINT -> amount
            UnitType.CUP -> amount * 2
            UnitType.OUNCE -> amount * 16
            UnitType.TABLE_SPOON -> amount * 32
            UnitType.TEA_SPOON -> amount * 96
            UnitType.LITER -> amount * 0.473176f
            UnitType.MILLILITER -> amount * 473.176f
            else -> throw IllegalArgumentException("Cannot convert pint to $type")
        }
    }

    private fun cupTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 16
            UnitType.QUART -> amount / 4
            UnitType.PINT -> amount / 2
            UnitType.CUP -> amount
            UnitType.OUNCE -> amount * 8
            UnitType.TABLE_SPOON -> amount * 16
            UnitType.TEA_SPOON -> amount * 48
            UnitType.LITER -> amount * 0.236588f
            UnitType.MILLILITER -> amount * 236.588f
            else -> throw IllegalArgumentException("Cannot convert cup to $type")
        }
    }

    private fun ounceTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 128
            UnitType.QUART -> amount / 32
            UnitType.PINT, UnitType.POUND -> amount / 16
            UnitType.CUP -> amount / 8
            UnitType.OUNCE -> amount
            UnitType.TABLE_SPOON -> amount * 2
            UnitType.TEA_SPOON -> amount * 6
            UnitType.LITER -> amount * 0.0295735f
            UnitType.MILLILITER -> amount * 29.5735f
            UnitType.GRAM -> amount * 28.3495f
        }
    }

    private fun tableSpoonTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 256
            UnitType.QUART -> amount / 64
            UnitType.PINT -> amount / 32
            UnitType.CUP -> amount / 16
            UnitType.OUNCE -> amount / 2
            UnitType.TABLE_SPOON -> amount
            UnitType.TEA_SPOON -> amount * 3
            UnitType.LITER -> amount * 0.0147868f
            UnitType.MILLILITER -> amount * 14.7868f
            else -> throw IllegalArgumentException("Cannot convert table spoon to $type")
        }
    }

    private fun teaSpoonTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount / 768
            UnitType.QUART -> amount / 192
            UnitType.PINT -> amount / 96
            UnitType.CUP -> amount / 48
            UnitType.OUNCE -> amount / 6
            UnitType.TABLE_SPOON -> amount / 3
            UnitType.TEA_SPOON -> amount
            UnitType.LITER -> amount * 0.00492892f
            UnitType.MILLILITER -> amount * 4.92892f
            else -> throw IllegalArgumentException("Cannot convert tea spoon to $type")
        }
    }

    private fun poundTo(type: UnitType): Float {
        return when(type) {
            UnitType.GRAM -> amount * 453.592f
            UnitType.POUND -> amount
            UnitType.OUNCE -> amount * 16
            else -> throw IllegalArgumentException("Cannot convert pound to $type")
        }
    }

    private fun literTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON -> amount * 0.264172f
            UnitType.QUART -> amount * 1.05669f
            UnitType.PINT -> amount * 2.11338f
            UnitType.CUP -> amount * 4.22675f
            UnitType.OUNCE -> amount * 33.814f
            UnitType.TABLE_SPOON -> amount * 67.628f
            UnitType.TEA_SPOON -> amount * 202.884f
            UnitType.LITER -> amount
            UnitType.MILLILITER -> amount * 1000
            else -> throw IllegalArgumentException("Cannot convert liter to $type")
        }
    }

    private fun milliLiterTo(type: UnitType): Float {
        return when(type) {
            UnitType.GALLON,
            UnitType.QUART,
            UnitType.PINT,
            UnitType.CUP,
            UnitType.OUNCE,
            UnitType.TABLE_SPOON,
            UnitType.TEA_SPOON -> literTo(type) / 1000.0f
            UnitType.LITER -> amount / 1000.0f
            UnitType.MILLILITER -> amount
            else -> throw IllegalArgumentException("Cannot convert milliliter to $type")
        }
    }

    private fun gramTo(type: UnitType): Float {
        return when(type) {
            UnitType.GRAM -> amount
            UnitType.POUND -> amount * 0.00220462f
            UnitType.OUNCE -> amount * 0.035274f
            else -> throw IllegalArgumentException("Cannot convert gram to $type")
        }
    }
}