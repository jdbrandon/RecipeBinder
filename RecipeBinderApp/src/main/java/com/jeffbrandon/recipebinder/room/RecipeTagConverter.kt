package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.enums.RecipeTag

class RecipeTagConverter private constructor() {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toInt(tags: Set<RecipeTag>): Int {
            var res = 0
            for (tag in tags) {
                res = res or (1 shl RecipeTag.values.indexOf(tag))
            }
            return res
        }

        @TypeConverter
        @JvmStatic
        fun toListRecipeTag(tags: Int): Set<RecipeTag> {
            val res = mutableSetOf<RecipeTag>()
            for (i in RecipeTag.values.indices) {
                if ((1 shl i) and tags != 0) res.add(RecipeTag.values[i])
            }
            return res
        }
    }
}
