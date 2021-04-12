package com.jeffbrandon.recipebinder.enums

import android.content.Context
import com.jeffbrandon.recipebinder.R

enum class RecipeTag(private val resId: Int) {
    INSTANT_POT(R.string.instant_pot),
    STOVE(R.string.stove_top),
    OVEN(R.string.oven),
    SOUS_VIDE(R.string.sous_vide),
    FAST(R.string.fast),
    EASY(R.string.easy),
    HEALTHY(R.string.healthy),
    VEGETARIAN(R.string.vegetarian),
    VEGAN(R.string.vegan),
    ENTREE(R.string.entree),
    SIDE(R.string.side),
    DESSERT(R.string.dessert),
    SOUP(R.string.dessert);

    fun getString(context: Context) = context.getString(resId)
}
