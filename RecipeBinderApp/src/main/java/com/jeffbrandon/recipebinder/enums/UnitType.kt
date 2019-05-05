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
}
