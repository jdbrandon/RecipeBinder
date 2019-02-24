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
    GRAM;

    override fun toString(): String {
        return this.name.toLowerCase().replace('_', ' ')
    }
}
