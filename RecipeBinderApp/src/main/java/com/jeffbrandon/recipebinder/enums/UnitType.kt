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
        if(this == NONE) return ""
        return this.name.toLowerCase().replace('_', ' ')
    }

    companion object {
        fun fromString(c: CharSequence): UnitType {
            return when(c) {
                "gal" -> GALLON
                "qt" -> QUART
                "p" -> PINT
                "c" -> CUP
                "oz" -> OUNCE
                "Tbsp" -> TABLE_SPOON
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
