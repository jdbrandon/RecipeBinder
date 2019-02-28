package com.jeffbrandon.recipebinder.room

import androidx.room.TypeConverter
import com.jeffbrandon.recipebinder.data.Ingredient
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class IngredientListConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toString(ingredients: List<Ingredient>): String {
            return when(ingredients.size) {
                0 -> ""
                else -> getJsonConverter().toJson(ingredients)
            }
        }

        @TypeConverter
        @JvmStatic
        fun toListIngredient(json: String): List<Ingredient> {
            json.run {
                if(isEmpty()) return listOf()
                return getJsonConverter().fromJson(this)!!
            }
        }

        @JvmStatic
        private fun getJsonConverter(): JsonAdapter<List<Ingredient>> {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            return moshi.adapter<List<Ingredient>>(Types.newParameterizedType(List::class.java, Ingredient::class.java))
        }
    }
}