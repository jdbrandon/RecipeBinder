package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.enums.RecipeTag

class RecipeTagConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toInt(tags: MutableList<RecipeTag>): Int {
            var res = 0
            for(tag in tags) {
                res = res or when(tag) {
                    RecipeTag.INSTANT_POT -> 1
                    RecipeTag.STOVE -> 1 shl 1
                    RecipeTag.OVEN -> 1 shl 2
                    RecipeTag.SOUS_VIDE -> 1 shl 3
                    RecipeTag.FAST -> 1 shl 4
                    RecipeTag.EASY -> 1 shl 5
                    RecipeTag.HEALTHY -> 1 shl 6
                    RecipeTag.VEGETARIAN -> 1 shl 7
                    RecipeTag.VEGAN -> 1 shl 8
                }
            }
            return res
        }

        @TypeConverter
        @JvmStatic
        fun toListRecipeTag(i: Int): MutableList<RecipeTag> {
            val res = mutableListOf<RecipeTag>()
            for(j in 0..RecipeTag.values().size) {
                when((1 shl j) and i) {
                    1 -> res.add(RecipeTag.INSTANT_POT)
                    1 shl 1 -> res.add(RecipeTag.STOVE)
                    1 shl 2 -> res.add(RecipeTag.OVEN)
                    1 shl 3 -> res.add(RecipeTag.SOUS_VIDE)
                    1 shl 4 -> res.add(RecipeTag.FAST)
                    1 shl 5 -> res.add(RecipeTag.EASY)
                    1 shl 6 -> res.add(RecipeTag.HEALTHY)
                    1 shl 7 -> res.add(RecipeTag.VEGETARIAN)
                    1 shl 8 -> res.add(RecipeTag.VEGAN)
                    else -> {
                    }
                }
            }
            return res
        }
    }
}