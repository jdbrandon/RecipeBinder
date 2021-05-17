package com.jeffbrandon.recipebinder.enums

import android.content.Context
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.FragmentAddIngredientBinding
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class UnitType(private val resId: Int, private val abbreviationResId: Int) {
    GALLON(R.string.gallon, R.string.abbreviation_gallon),
    QUART(R.string.quart, R.string.abbreviation_quart),
    PINT(R.string.pint, R.string.abbreviation_pint),
    CUP(R.string.cup, R.string.abbreviation_cup),
    OUNCE(R.string.ounce, R.string.abbreviation_ounce),
    TABLE_SPOON(R.string.table_spoon, R.string.abbreviation_tablespoon),
    TEA_SPOON(R.string.tea_spoon, R.string.abbreviation_teaspoon),
    POUND(R.string.pound, R.string.abbreviation_pound),
    LITER(R.string.liter, R.string.abbreviation_liter),
    MILLILITER(R.string.milliliter, R.string.abbreviation_milliliter),
    GRAM(R.string.gram, R.string.abbreviation_gram),
    NONE(-1, -1);

    fun getString(context: Context, useLongUnitString: Boolean = false): String {
        return when (this) {
            NONE -> ""
            else -> context.getString(if (useLongUnitString) resId else abbreviationResId)
        }
    }

    companion object {
        fun FragmentAddIngredientBinding.unitMap() = mapOf(
            GALLON to gallonChip,
            QUART to quartChip,
            PINT to pintChip,
            CUP to cupChip,
            OUNCE to ounceChip,
            TABLE_SPOON to tbspChip,
            TEA_SPOON to tspChip,
            POUND to poundChip,
            LITER to literChip,
            MILLILITER to milliliterChip,
            GRAM to gramChip,
        )
    }
}
