package com.jeffbrandon.recipebinder.enums

import android.content.Context
import com.jeffbrandon.recipebinder.R

enum class UnitType(private val abbreviationResId: Int) {
    GALLON(R.string.abbreviation_gallon),
    QUART(R.string.abbreviation_quart),
    PINT(R.string.abbreviation_pint),
    CUP(R.string.abbreviation_cup),
    OUNCE(R.string.abbreviation_ounce),
    TABLE_SPOON(R.string.abbreviation_tablespoon),
    TEA_SPOON(R.string.abbreviation_teaspoon),
    POUND(R.string.abbreviation_pound),
    LITER(R.string.abbreviation_liter),
    MILLILITER(R.string.abbreviation_milliliter),
    GRAM(R.string.abbreviation_gram),
    NONE(-1);

    fun getString(context: Context): String {
        return when (this) {
            NONE -> ""
            else -> context.getString(abbreviationResId)
        }
    }
}
