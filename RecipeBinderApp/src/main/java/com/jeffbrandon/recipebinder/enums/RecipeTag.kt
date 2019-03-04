package com.jeffbrandon.recipebinder.enums

import android.content.Context
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

enum class RecipeTag {
    INSTANT_POT,
    STOVE,
    OVEN,
    SOUS_VIDE,
    FAST,
    EASY,
    HEALTHY,
    VEGETARIAN,
    VEGAN;

    override fun toString(): String {
        return when(this) {
            INSTANT_POT -> "Instant Pot"
            STOVE -> "Stove Top"
            OVEN -> "Oven"
            SOUS_VIDE -> "Sous Vide"
            FAST -> "Fast"
            EASY -> "Easy"
            HEALTHY -> "Healthy"
            VEGETARIAN -> "Vegetarian"
            VEGAN -> "Vegan"
        }
    }

    fun toChipView(context: Context): Chip {
        return Chip(context).apply {
            text = this@RecipeTag.toString()
            layoutParams =
                ChipGroup.LayoutParams(ChipGroup.LayoutParams.WRAP_CONTENT, ChipGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
