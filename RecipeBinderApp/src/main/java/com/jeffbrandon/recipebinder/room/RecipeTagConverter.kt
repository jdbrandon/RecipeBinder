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
                res = res or (1 shl RecipeTag.values().indexOf(tag))
            }
            return res
        }

        @TypeConverter
        @JvmStatic
        fun toListRecipeTag(tags: Int): MutableList<RecipeTag> {
            val res = mutableListOf<RecipeTag>()
            for(i in 0..RecipeTag.values().size) {
                if((1 shl i) and tags != 0)
                    res.add(RecipeTag.values()[i])
            }
            return res
        }
    }
}