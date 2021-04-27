package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.dagger.MoshiModule
import com.jeffbrandon.recipebinder.data.Ingredient

class IngredientListConverter private constructor() {

    companion object {
        @TypeConverter
        @JvmStatic
        fun toString(ingredients: List<Ingredient>): String {
            return when (ingredients.size) {
                0 -> ""
                else -> MoshiModule.ingredientConverter.toJson(ingredients)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListIngredient(json: String): List<Ingredient> {
            json.run {
                if (isEmpty()) return listOf()
                return MoshiModule.ingredientConverter.fromJson(this) ?: error("failed to parse ingredients")
            }
        }
    }
}
