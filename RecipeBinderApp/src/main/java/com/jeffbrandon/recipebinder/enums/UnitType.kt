package com.jeffbrandon.recipebinder.enums

enum class UnitType {
    GALLON,
    QUART,
    PINT,
    CUP,
    OUNCE,
    TABLE_SPOON,
    TEA_SPOON,
    POUND,
    LITER,
    MILLILITER,
    GRAM,
    NONE;

    override fun toString(): String {
        return when(this) {
            GALLON -> "gal"
            QUART -> "qt"
            PINT -> "pt"
            CUP -> "c"
            OUNCE -> "oz"
            TABLE_SPOON -> "tbp"
            TEA_SPOON -> "tsp"
            POUND -> "lb"
            LITER -> "l"
            MILLILITER -> "ml"
            GRAM -> "g"
            NONE -> ""
        }
    }

    companion object {
        fun fromString(s: String): UnitType {
            return when(s) {
                "gal" -> GALLON
                "qt" -> QUART
                "pt" -> PINT
                "c" -> CUP
                "oz" -> OUNCE
                "tbp" -> TABLE_SPOON
                "tsp" -> TEA_SPOON
                "lb" -> POUND
                "l" -> LITER
                "ml" -> MILLILITER
                "g" -> GRAM
                else -> NONE
            }
        }
    }
}
