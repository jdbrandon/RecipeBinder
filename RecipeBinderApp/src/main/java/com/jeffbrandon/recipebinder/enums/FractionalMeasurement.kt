package com.jeffbrandon.recipebinder.enums

@SuppressWarnings("MagicNumber")
enum class FractionalMeasurement(private val value: Float) {
    ZERO(0f),
    QUARTER(0.25f),
    THIRD(0.33f),
    HALF(0.5f),
    THIRD_TWO(0.66f),
    QUARTER_THREE(0.75f);

    fun toFloat() = value
}
