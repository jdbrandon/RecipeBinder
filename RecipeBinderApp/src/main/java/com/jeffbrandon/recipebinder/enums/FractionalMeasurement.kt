package com.jeffbrandon.recipebinder.enums

import com.jeffbrandon.recipebinder.databinding.FragmentAddIngredientBinding

@SuppressWarnings("MagicNumber")
enum class FractionalMeasurement(private val value: Float) {
    ZERO(0f),
    QUARTER(0.25f),
    THIRD(0.33f),
    HALF(0.5f),
    THIRD_TWO(0.66f),
    QUARTER_THREE(0.75f),
    ROUND_UP(1f);

    fun toFloat() = value

    companion object {
        fun FragmentAddIngredientBinding.fractionViewMap() = mapOf(
            QUARTER to chipInputQuarter,
            THIRD to chipInputThird,
            HALF to chipInputHalf,
            THIRD_TWO to chipInput2Thirds,
            QUARTER_THREE to chipInput3Quarter,
        )
    }
}
