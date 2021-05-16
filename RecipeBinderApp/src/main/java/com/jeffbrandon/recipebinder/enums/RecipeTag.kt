package com.jeffbrandon.recipebinder.enums

import android.content.Context
import com.jeffbrandon.recipebinder.R
import com.jeffbrandon.recipebinder.databinding.EditTagsBinding
import com.squareup.moshi.JsonClass

/**
 * We can store up to 32 of these without changing the database implementation
 * @see com.jeffbrandon.recipebinder.room.RecipeTagConverter
 */
@JsonClass(generateAdapter = false)
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
    DAIRY(R.string.dairy),
    PEANUTS(R.string.peanuts),
    SOY(R.string.soy),
    WHEAT(R.string.wheat),
    TREE_NUTS(R.string.tree_nuts),
    SHELLFISH(R.string.shellfish),
    SESAME(R.string.sesame),
    ENTREE(R.string.entree),
    SIDE(R.string.side),
    DESSERT(R.string.dessert),
    SOUP(R.string.dessert),
    MIXER(R.string.mixer),
    FOOD_PROCESSOR(R.string.food_processor),
    BLENDER(R.string.blender),
    IMMERSION_BLENDER(R.string.blender),
    TORCH(R.string.torch),
    MORTAR_PESTLE(R.string.mortar_amp_pestle);

    fun getString(context: Context) = context.getString(resId)

    companion object {
        val values by lazy { values() }

        /**
         * Convenience method that makes binding these a lot simpler
         */
        fun EditTagsBinding.recipeMap() = mapOf(
            INSTANT_POT to chipInstantPot,
            STOVE to chipStove,
            OVEN to chipOven,
            SOUS_VIDE to chipSousVide,
            FAST to chipFast,
            EASY to chipEasy,
            HEALTHY to chipHealthy,
            VEGETARIAN to chipVegetarian,
            VEGAN to chipVegan,
            DAIRY to chipDairy,
            PEANUTS to chipPeanuts,
            SOY to chipSoy,
            WHEAT to chipWheat,
            TREE_NUTS to chipTreeNuts,
            SHELLFISH to chipShellfish,
            SESAME to chipSesame,
            ENTREE to chipEntree,
            SIDE to chipSide,
            DESSERT to chipDessert,
            SOUP to chipSoup,
            MIXER to chipMixer,
            FOOD_PROCESSOR to chipFoodProcessor,
            BLENDER to chipBlender,
            IMMERSION_BLENDER to chipImmersionBlender,
            TORCH to chipTorch,
            MORTAR_PESTLE to chipMortarPestle,
        )
    }
}
