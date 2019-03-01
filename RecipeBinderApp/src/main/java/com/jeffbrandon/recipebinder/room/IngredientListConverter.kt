package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.jeffbrandon.recipebinder.moshi.MoshiSingletons

class IngredientListConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toString(ingredients: List<Ingredient>): String {
            return when(ingredients.size) {
                0 -> ""
                else -> MoshiSingletons.ingredientConverter.toJson(ingredients)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListIngredient(json: String): List<Ingredient> {
            json.run {
                if(isEmpty()) return listOf()
                return MoshiSingletons.ingredientConverter.fromJson(this)!!
            }
        }
    }
}